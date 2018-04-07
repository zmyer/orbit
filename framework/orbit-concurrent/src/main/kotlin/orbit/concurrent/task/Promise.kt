/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task

import orbit.concurrent.task.impl.AbstractTaskImpl
import orbit.util.tries.Try
import java.util.concurrent.atomic.AtomicBoolean

class Promise<T> : AbstractTaskImpl<T, T>() {
    private val hasFired = AtomicBoolean(false)

    override fun operator(item: Try<T>) {
        item.onFailure {
            completeExceptionally(it)
        }.onSuccess {
            complete(it)
        }
    }

    /**
     * Completes the [Promise] successfully with the supplied result.
     *
     * @param result The result to complete with.
     * @throws IllegalStateException If the promise has already been completed.
     */
    fun complete(result: T) {
        if (hasFired.compareAndSet(false, true)) {
            publish(Try.success(result))
        } else {
            throw IllegalStateException("Promise has already been completed. A promise may only be completed once.")
        }
    }

    /**
     * Completes the [Promise] exceptionally with the supplied result.
     *
     * @param result The result to complete with.
     * @throws IllegalStateException If the promise has already been completed.
     */
    fun completeExceptionally(result: Throwable) {
        if (hasFired.compareAndSet(false, true)) {
            publish(Try.failed(result))
        } else {
            throw IllegalStateException("Promise has already been completed. A promise may only be completed once.")
        }
    }
}