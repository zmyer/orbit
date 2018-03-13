/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task

import orbit.concurrent.flow.Publisher
import orbit.concurrent.job.JobManager
import orbit.concurrent.job.JobManagers
import orbit.concurrent.task.operator.TaskAllOf
import orbit.concurrent.task.operator.TaskAnyOf
import orbit.concurrent.task.operator.TaskApply
import orbit.concurrent.task.operator.TaskAwait
import orbit.concurrent.task.operator.TaskDoAlways
import orbit.concurrent.task.operator.TaskDoOnError
import orbit.concurrent.task.operator.TaskDoOnValue
import orbit.concurrent.task.operator.TaskFlatMap
import orbit.concurrent.task.operator.TaskFromCompletableFuture
import orbit.concurrent.task.operator.TaskJust
import orbit.concurrent.task.operator.TaskMap
import orbit.concurrent.task.operator.TaskRunOn
import orbit.util.tries.Try
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/**
 * Represents that a unit of asynchronous work will be completed and the value will be made available in the future.
 * [Task]s are guaranteed to complete and may complete successfully or exceptionally.
 */
abstract class Task<T>: Publisher<T> {
    /**
     * Upon this [Task]'s completion, executes the given function and returns a new [Task] with the result of the
     * original.
     *
     * @param body The function to run.
     * @return The future.
     */
    fun doAlways(body: (Try<T>) -> Unit): Task<T> =
            TaskDoAlways(body).also { this.subscribe(it) }
    fun doAlways(body: Consumer<Try<T>>): Task<T> =
            doAlways({body.accept(it)})
    fun doAlways(body: Runnable): Task<T> =
            doAlways({body.run()})

    /**
     * Upon this [Task]'s success, executes the given function and returns a new [Task] with the result of the original.
     *
     * @param body The function to run on success.
     * @return The future.
     */
    fun doOnValue(body: (T) -> Unit): Task<T> =
            TaskDoOnValue(body).also { this.subscribe(it) }
    fun doOnValue(body: Consumer<T>): Task<T> =
            doOnValue({body.accept(it)})
    fun doOnValue(body: Runnable): Task<T> =
            doOnValue({body.run()})

    /**
     * Upon this [Task]'s failure, executes the given function and returns a new [Task] with the result of the original.
     *
     * @param body The function to run on failure.
     * @return The task.
     */
    fun doOnError(body: (Throwable) -> Unit): Task<T> =
            TaskDoOnError<T>(body).also { this.subscribe(it) }
    fun doOnError(body: Consumer<Throwable>): Task<T> =
            doOnError({body.accept(it)})
    fun doOnError(body: Runnable): Task<T> =
            doOnError({body.run()})


    /**
     * Synchronously maps the value of this [Task] to a new value and returns a completed [Task].
     *
     * If the initial [Task] is in a failed state the new [Task] is failed with the same [Throwable].
     *
     * @param body The mapping function.
     * @return The completed task with the new value.
     */
    fun <R> map(body: (T) -> R): Task<R> =
            TaskMap(body).also { this.subscribe(it) }

    /**
     * Asynchronously maps the value of this [Task] to another [Task] and flattens the result of the latter.
     *
     * If the initial [Task] is in a failed state the new [Task] is failed with the same [Throwable].
     *
     * @param body The mapping function.
     * @return A new asynchronous [Task] with the mapped value.
     */
    fun <O> flatMap(body: (T) -> Task<O>): Task<O> =
            TaskFlatMap(body).also { this.subscribe(it) }

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
            TaskRunOn<T>(jobManager).also { this.subscribe(it) }

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
    fun runOn(body: () -> JobManager): Task<T> = runOn(body())

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
            TaskAwait<T>().also { this.subscribe(it) }.waitOnLatch()

    /**
     * Returns true if this [Task] is complete either successfully or exceptionally.
     *
     * @return true if this [Task] is complete, otherwise false.
     */
    abstract fun isComplete(): Boolean

    /**
     * Returns true if this [Task] completed successfully. Returns false if the [Task] completed exceptionally
     * or is not yet completed.
     *
     * @return true if this [Task] was successful, otherwise false.
     */
    abstract fun isSuccessful(): Boolean

    /**
     * Returns true if this [Task] completed with an error. Returns false if the [Task] completed successfully.
     * or is not yet completed.
     *
     * @return true if this [Task] was an error, otherwise false.
     */
    abstract fun isError(): Boolean

    /**
     * Converts this task into a Java [CompletableFuture] which is resolved based on the [Task] result.
     *
     * @return A Java [CompletableFuture].
     */
    fun asCompletableFuture(): CompletableFuture<T> {
        val cf = CompletableFuture<T>()

        this.doOnValue {
            cf.complete(it)
        }.doOnError {
            cf.completeExceptionally(it)
        }

        return cf
    }

    companion object {

        /**
         * Creates a [Task] which executes on the specified [JobManager].
         *
         * @param jobManager The [JobManager] on which this block should be executed.
         * @return The [Task].
         * @param body The function to be executed by the [JobManager].
         */
        operator fun <V> invoke(jobManager: JobManager, body: () -> V) = create(jobManager, body)
        operator fun <V> invoke(body: () -> V) = create(body)

        /**
         * Creates a [Task] which executes on the specified [JobManager].
         *
         * @param jobManager The [JobManager] on which this block should be executed
         * @param body The function to be executed by the [JobManager].
         * @return The [Task].
         */
        @JvmStatic
        fun <V> create(jobManager: JobManager, body: () -> V): Task<V> = TaskApply(jobManager, body)
        @JvmStatic
        fun <V> create(body: () -> V): Task<V> = create(JobManagers.parallel(), body)
        @JvmStatic
        fun create(jobManager: JobManager, body: Runnable): Task<Unit> = create(jobManager ,{ body.run() })
        @JvmStatic
        fun create(body: Runnable): Task<Unit> = create(JobManagers.parallel(), body)



        /**
         * Creates a [Task] which is immediately completed with the specified value.
         *
         * Callbacks will run on the calling thread. See [Task.runOn] to execute on another manager.
         *
         * @param value The value for the [Task] to be resolved with.
         * @return The completed [Task].
         */
        @JvmStatic
        fun <V> just(value: V): Task<V> = TaskJust(Try.success(value))

        /**
         * Creates a [Task] which is immediately failed with the specified value.
         *
         * Callbacks will run on the calling thread. See [Task.runOn] to execute on another manager.
         *
         * @param t The [Throwable] for the [Task] to be resolved with.
         * @return The failed [Task].
         */
        @JvmStatic
        fun <V> fail(t: Throwable): Task<V> = TaskJust(Try.failed(t))

        /**
         * Returns an empty [Task] that is already completed with an empty result.
         *
         * Callbacks will run on the calling thread. See [Task.runOn] to execute on another manager.
         *
         * @return The empty [Task].
         */
        @JvmStatic
        fun empty(): Task<Unit> = Task.just(Unit)

        /**
         * Returns a new [Task] that is completed when all of the supplied [Task]s are completed.
         * If any of the supplied [Task]s complete exceptionally then the new [Task] is completed with one of those
         * exceptions, otherwise it is completed with [Unit].
         *
         * @param tasks The [Task]s.
         * @return The new [Task].
         */
        @JvmStatic
        fun allOf(tasks: Iterable<Task<*>>): Task<Unit> = TaskAllOf(tasks)

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
        fun anyOf(tasks: Iterable<Task<*>>): Task<Unit> = TaskAnyOf(tasks)

        @JvmStatic
        fun anyOf(vararg tasks: Task<*>): Task<Unit> = anyOf(tasks.asIterable())

        /**
         * Creates a [Task] which is completed based on the result of a [CompletableFuture].
         *
         * @param cf The [CompletableFuture] to listen on.
         * @return The new [Task].
         */
        @JvmStatic
        fun <V> fromCompletableFuture(cf: CompletableFuture<V>): Task<V> =
                TaskFromCompletableFuture(cf)

        /**
         * Creates a new [Promise].
         *
         * @return The new [Promise].
         */
        @JvmStatic
        fun <V> promise() = Promise<V>()
    }
}