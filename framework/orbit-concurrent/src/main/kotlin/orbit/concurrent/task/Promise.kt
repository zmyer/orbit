/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task

import orbit.util.exception.InvalidStateException
import orbit.util.tries.Try
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Represents a [Task] that must be completed externally.
 */
class Promise<T>: Task<T>() {
    private val hasFired = AtomicBoolean(false)

    /**
     * Completes the [Promise] successfully with the supplied result.
     *
     * @param result The result to complete with.
     * @throws InvalidStateException If the promise has already been completed.
     */
    fun complete(result: T) {
        if(hasFired.compareAndSet(false, true)) {
            value = Try.success(result)
            triggerListeners()
        } else {
            throw InvalidStateException("Promise has already been completed. A promise may only be completed once.")
        }
    }

    /**
     * Completes the [Promise] exceptionally with the supplied result.
     *
     * @param result The result to complete with.
     * @throws InvalidStateException If the promise has already been completed.
     */
    fun completeExceptionally(result: Throwable) {
        if(hasFired.compareAndSet(false, true)) {
            value = Try.failed(result)
            triggerListeners()
        } else {
            throw InvalidStateException("Promise has already been completed. A promise may only be completed once.")
        }
    }
}