/*
 Copyright (C) 2018 Electronic Arts Inc.  All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1.  Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2.  Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
     its contributors may be used to endorse or promote products derived
     from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package orbit.concurrent.task

import orbit.concurrent.job.JobManager
import orbit.concurrent.job.JobManagers
import orbit.concurrent.task.operator.TaskAllOfOperator
import orbit.concurrent.task.operator.TaskAnyOfOperator
import orbit.concurrent.task.operator.TaskApplyOperator
import orbit.concurrent.task.operator.TaskAwaitOperator
import orbit.concurrent.task.operator.TaskDoAlwaysOperator
import orbit.concurrent.task.operator.TaskFromCompletableFutureOperator
import orbit.concurrent.task.operator.TaskFlatMapOperator
import orbit.concurrent.task.operator.TaskMapOperator
import orbit.concurrent.task.operator.TaskDoOnFailureOperator
import orbit.concurrent.task.operator.TaskDoOnSuccessOperator
import orbit.concurrent.task.operator.TaskOperator
import orbit.concurrent.task.operator.TaskRunOnOperator
import orbit.concurrent.task.operator.TaskImmediateValueOperator
import orbit.util.tries.Failure
import orbit.util.tries.Success
import orbit.util.tries.Try
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock

/**
 * Represents that a unit of asynchronous work will be completed and the value will be made available in the future.
 * [Task]s are guaranteed to complete and may complete successfully or exceptionally.
 */
abstract class Task<T> {
    private val listeners = ConcurrentLinkedQueue<TaskOperator<T, *>>()
    private val lock = ReentrantLock()

    @Volatile
    internal var value: Try<T>? = null

    internal fun triggerListeners() {
        tailrec fun drainQueue(opVal: Try<T>) {
            val polled = listeners.poll()
            if(polled != null) {
                executeListener(polled, opVal)
                drainQueue(opVal)
            }
        }

        val valResult = value
        if(valResult != null) {
            if(lock.tryLock()) {
                try {
                    drainQueue(valResult)
                } finally {
                    lock.unlock()
                }
            }
        }
    }

    internal open fun executeListener(listener: TaskOperator<T, *>, triggerVal: Try<T>) {
        listener.onFulfilled(triggerVal)
    }

    private fun addListener(taskOperator: TaskOperator<T, *>) {
        val valResult = value
        if(valResult == null) {
            listeners.add(taskOperator)
            triggerListeners()
        } else {
            executeListener(taskOperator, valResult)
        }

    }

    /**
     * Upon this [Task]'s completion, executes the given function and returns a new [Task] with the result of the
     * original.
     *
     * @param body The function to run.
     * @return The task.
     */
    infix fun doAlways(body: (Try<T>) -> Unit): Task<T> =
            TaskDoAlwaysOperator(body).apply { addListener(this) }

    /**
     * Upon this [Task]'s success, executes the given function and returns a new [Task] with the result of the original.
     *
     * @param body The function to run on success.
     * @return The task.
     */
    infix fun doOnValue(body: (T) -> Unit): Task<T> =
            TaskDoOnSuccessOperator(body).apply { addListener(this) }

    /**
     * Upon this [Task]'s failure, executes the given function and returns a new [Task] with the result of the original.
     *
     * @param body The function to run on failure.
     * @return The task.
     */
    infix fun doOnError(body: (Throwable) -> Unit): Task<T> =
            TaskDoOnFailureOperator<T>(body).apply { addListener(this) }

    /**
     * Creates a new [Task] with the result of the current [Task] which forces operators to run on the specified
     * [JobManager].
     *
     * CAUTION: Only operators applied directly to this [Task] are guaranteed to run on the specifieid [JobManager].
     * Further calls are free to run on other threads.
     *
     * @param jobManager The target [JobManager].
     * @return The [Task].
     */
    fun runOn(jobManager: JobManager): Task<T> =
            TaskRunOnOperator<T>(jobManager).apply { addListener(this) }

    /**
     * Creates a new [Task] with the result of the current [Task] which forces operators to run on the specified
     * [JobManager]
     *
     * CAUTION: Only operators applied directly to this [Task] are guaranteed to run on the specifieid [JobManager].
     * Further calls are free to run on other threads.
     *
     * @param body A function which returns the desired target [JobManager].
     * @return The [Task].
     */
    infix fun runOn(body: () -> JobManager): Task<T> = runOn(body())

    /**
     * Synchronously maps the value of this [Task] to a new value and returns a completed [Task].
     *
     * If the initial [Task] is in a failed state the new [Task] is failed with the same [Throwable].
     *
     * @param body The mapping function.
     * @return The completed task with the new value.
     */
    infix fun <O> map(body: (T) -> O): Task<O> =
            TaskMapOperator(body).apply { addListener(this) }

    /**
     * Asynchronously maps the value of this [Task] to another [Task] and flattens the result of the latter.
     *
     * If the initial [Task] is in a failed state the new [Task] is failed with the same [Throwable].
     *
     * @param body The mapping function.
     * @return A new asynchronous [Task] with the mapped value.
     */
    infix fun <O> flatMap(body: (T) -> Task<O>): Task<O> =
            TaskFlatMapOperator(body).apply { addListener(this) }

    /**
     * Causes the current thread to wait for the [Task] to be completed.
     *
     * Upon [Task] completion the successful completion value is returned of the failure [Throwable] is raised.
     * CAUTION: Use of this method should generally be avoided as it blocks the current thread.
     *
     * @return The value of the completed task if successful.
     * @throws Throwable The [Throwable] of the failed tasked if failed.
     */
    fun await(): T =
            TaskAwaitOperator<T>().apply { addListener(this) }.waitOnLatch()

    /**
     * Converts this task into a Java [CompletableFuture] which is resolved based on the [Task] result.
     *
     * @return A Java [CompletableFuture].
     */
    fun asCompletableFuture(): CompletableFuture<T> {
        val cf = CompletableFuture<T>()

        this doOnValue {
            cf.complete(it)
        } doOnError {
            cf.completeExceptionally(it)
        }

        return cf
    }

    /**
     * Returns true if this [Task] is complete either successfully or exceptionally.
     *
     * @return true if this [Task] is complete, otherwise false.
     */
    fun isComplete() = value != null

    /**
     * Returns true if this [Task] completed successfully. Returns false if the [Task] completed exceptionally
     * or is not yet completed.
     *
     * @return true if this [Task] was successful, otherwise false.
     */
    fun isSuccessful() = when(value) {
        is Success -> true
        is Failure -> false
        null -> false
    }

    /**
     * Returns true if this [Task] completed exceptionally. Returns false if the [Task] completed successfully
     * or is not yet completed.
     *
     * @return true if this [Task] was exceptional, otherwise false.
     */
    fun isExceptional() = when(value) {
        is Failure -> true
        is Success -> false
        null -> false
    }

    companion object {
        private val EMPTY_TASK = just(Unit)

        /**
         * Creates a [Task] which executes on the default [JobManager].
         *
         * @param body The function to be executed by the [JobManager].
         * @return The [Task].
         */
        operator fun <V> invoke(body: () -> V) = create(body)

        /**
         * Creates a [Task] which executes on the specified [JobManager].
         *
         * @param jobManager The [JobManager] on which this block should be executed.
         * @return The [Task].
         * @param body The function to be executed by the [JobManager].
         */
        operator fun <V> invoke(jobManager: JobManager, body: () -> V) = create(jobManager, body)

        /**
         * Creates a [Task] which executes on the default [JobManager].
         *
         * @param body The function to be executed by the [JobManager].
         * @return The [Task].
         */
        @JvmStatic
        fun <V> create(body: () -> V): Task<V> = create(JobManagers.parallel(), body)

        /**
         * Creates a [Task] which executes on the specified [JobManager].
         *
         * @param jobManager The [JobManager] on which this block should be executed
         * @param body The function to be executed by the [JobManager].
         * @return The [Task].
         */
        @JvmStatic
        fun <V> create(jobManager: JobManager, body: () -> V): Task<V> = TaskApplyOperator(jobManager, body)

        /**
         * Creates a [Task] which is immediately completed with the specified value.
         *
         * Callbacks will run on the calling thread. See [Task.forceJobManager] to execute on another manager.
         *
         * @param value The value for the [Task] to be resolved with.
         * @return The completed [Task].
         */
        @JvmStatic
        fun <V> just(value: V): Task<V> = TaskImmediateValueOperator(Try.success(value))

        /**
         * Creates a [Task] which is immediately failed with the specified value.
         *
         * Callbacks will run on the calling thread. See [Task.forceJobManager] to execute on another manager.
         *
         * @param t The [Throwable] for the [Task] to be resolved with.
         * @return The failed [Task].
         */
        @JvmStatic
        fun <V> fail(t: Throwable): Task<V> = TaskImmediateValueOperator(Try.failed(t))

        /**
         * Returns an empty [Task] that is already completed with an empty result.
         *
         * Callbacks will run on the calling thread. See [Task.forceJobManager] to execute on another manager.
         *
         * @return The empty [Task].
         */
        @JvmStatic
        fun empty(): Task<Unit> = EMPTY_TASK

        /**
         * Returns a new [Task] that is completed when all of the supplied [Task]s are completed.
         * If any of the supplied [Task]s complete exceptionally then the new [Task] is completed with one of those
         * exceptions, otherwise it is completed with [Unit].
         *
         * @param tasks The [Task]s.
         * @return The new [Task].
         */
        @JvmStatic
        fun allOf(tasks: Iterable<Task<*>>): Task<Unit> = TaskAllOfOperator(tasks)

        /**
         * Returns a new [Task] that is completed when all of the supplied [Task]s are completed.
         * If any of the supplied [Task]s complete exceptionally then the new [Task] is completed with one of those
         * exceptions, otherwise it is completed with [Unit].
         *
         * @param tasks The [Task]s.
         * @return The new [Task].
         */
        @JvmStatic
        fun allOf(vararg tasks: Task<*>) = allOf(tasks.asIterable())

        /**
         * Returns a new [Task] that is completed when any of the supplied [Task]s are completed.
         * The new [Task] is always completed successfully even if the first supplied [Task] to complete completed
         * exceptionally.
         *
         * @param tasks The [Task]s.
         * @return The new [Task].
         */
        @JvmStatic
        fun anyOf(tasks: Iterable<Task<*>>): Task<Unit> = TaskAnyOfOperator(tasks)

        /**
         * Returns a new [Task] that is completed when any of the supplied [Task]s are completed.
         * The new [Task] is always completed successfully even if the first supplied [Task] to complete completed
         * exceptionally.
         *
         * @param tasks The [Task]s.
         * @return The new [Task].
         */
        @JvmStatic
        fun anyOf(vararg tasks: Task<*>): Task<Unit> = anyOf(tasks.asIterable())

        /**
         * Creates a [Task] which is completed based on the result of a [CompletableFuture].
         *
         * @param cf The [CompletableFuture] to listen on.
         * @return The new [Task].
         */
        @JvmStatic
        fun <V> fromCompletableFuture(cf: CompletableFuture<V>): Task<V> = TaskFromCompletableFutureOperator(cf)
    }
}