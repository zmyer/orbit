/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.impl

import orbit.concurrent.flow.Processor
import orbit.concurrent.flow.Subscriber
import orbit.concurrent.task.Task
import orbit.concurrent.task.TaskContext
import orbit.util.tries.Failure
import orbit.util.tries.Success
import orbit.util.tries.Try
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.locks.ReentrantLock

abstract class AbstractTaskImpl<T, R> internal constructor(): Processor<T, R>, Task<R>() {
    private val listeners = ConcurrentLinkedQueue<Subscriber<R>>()
    private val lock = ReentrantLock()

    @Volatile
    private var cachedValue: Try<R>? = null

    @Volatile
    private var taskCompletionContext: TaskContext? = null

    override fun subscribe(subscriber: Subscriber<R>) {
        val valResult = cachedValue
        if (valResult == null) {
            listeners.add(subscriber)
            broadcast()
        } else {
            sendTo(subscriber, valResult)
        }
    }

    override fun onNext(item: Try<T>) {
        taskCompletionContext = TaskContext.current()
        operator(item)
    }

    protected abstract fun operator(item: Try<T>)

    protected open fun publish(item: Try<R>) {
        cachedValue = item
        broadcast()
    }

    protected open fun broadcast() {
        tailrec fun drainQueue(opVal: Try<R>) {
            val polled = listeners.poll()
            if (polled != null) {
                sendTo(polled, opVal)
                drainQueue(opVal)
            }
        }

        val valResult = cachedValue
        if (valResult != null) {
            if (lock.tryLock()) {
                try {
                    drainQueue(valResult)
                } finally {
                    lock.unlock()
                }
            }
        }
    }

    protected open fun sendTo(listener: Subscriber<R>, opVal: Try<R>) {
        taskCompletionContext?.push()
        listener.onNext(opVal)
        taskCompletionContext?.pop()
    }

    override fun isComplete(): Boolean =
            cachedValue != null

    override fun isSuccessful(): Boolean = when (cachedValue) {
        is Success -> true
        is Failure -> false
        null -> false
    }

    override fun isError(): Boolean = when (cachedValue) {
        is Failure -> true
        is Success -> false
        null -> false
    }

}