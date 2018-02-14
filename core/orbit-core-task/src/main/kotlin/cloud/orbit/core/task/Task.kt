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

package cloud.orbit.core.task

import cloud.orbit.core.concurrent.JobManager
import cloud.orbit.core.concurrent.JobManagers
import cloud.orbit.core.task.operator.TaskApplyOperator
import cloud.orbit.core.task.operator.TaskAwaitOperator
import cloud.orbit.core.task.operator.TaskFlatMapOperator
import cloud.orbit.core.task.operator.TaskHandleOperator
import cloud.orbit.core.task.operator.TaskMapOperator
import cloud.orbit.core.task.operator.TaskOnFailureOperator
import cloud.orbit.core.task.operator.TaskOnSuccessOperator
import cloud.orbit.core.task.operator.TaskOperator
import cloud.orbit.core.task.operator.TaskForceJobManager
import cloud.orbit.core.tries.Try
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock

abstract class Task<T> {
    private val listeners = ConcurrentLinkedQueue<TaskOperator<T, *>>()
    private val lock = ReentrantLock()

    @Volatile
    internal var value: Try<T>? = null

    internal open fun triggerListeners() {
        tailrec fun drainQueue(opVal: Try<T>) {
            val polled = listeners.poll()
            if(polled != null) {
                polled.fulfilled(opVal)
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

    private fun addListener(taskOperator: TaskOperator<T, *>) {
        listeners.add(taskOperator)
        triggerListeners()
    }

    infix fun handle(body: (Try<T>) -> Unit): Task<T> {
        val taskOperator = TaskHandleOperator(body)
        addListener(taskOperator)
        return taskOperator
    }

    infix fun onSuccess(body: (T) -> Unit): Task<T> {
        val taskOperator = TaskOnSuccessOperator(body)
        addListener(taskOperator)
        return taskOperator
    }

    infix fun onFailure(body: (Throwable) -> Unit): Task<T> {
        val taskOperator = TaskOnFailureOperator<T>(body)
        addListener(taskOperator)
        return taskOperator
    }

    fun forceJobManager(jobManager: JobManager): Task<T> {
        val taskOperator = TaskForceJobManager<T>(jobManager)
        addListener(taskOperator)
        return taskOperator
    }

    infix fun forceJobManager(body: () -> JobManager): Task<T> {
        return forceJobManager(body())
    }

    infix fun <O> map(body: (T) -> O): Task<O> {
        val taskOperator = TaskMapOperator(body)
        addListener(taskOperator)
        return taskOperator
    }

    infix fun <O> flatMap(body: (T) -> Task<O>): Task<O> {
        val taskOperator = TaskFlatMapOperator(body)
        addListener(taskOperator)
        return taskOperator
    }

    fun await(): T {
        val taskOperator = TaskAwaitOperator<T>()
        addListener(taskOperator)
        return taskOperator.waitOnLatch()
    }

    companion object {
        operator fun <V> invoke(body: () -> V) = create(body)
        operator fun <V> invoke(jobManager: JobManager, body: () -> V) = create(jobManager, body)

        @JvmStatic
        fun <V> create(body: () -> V): Task<V> = create(JobManagers.parallel(), body)

        @JvmStatic
        fun <V> create(jobManager: JobManager, body: () -> V): Task<V> = TaskApplyOperator(jobManager, body)
    }
}