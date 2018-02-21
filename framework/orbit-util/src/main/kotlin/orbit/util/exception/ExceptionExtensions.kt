/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.exception

/**
 * Checks whether the specified exception type (cause) is in the chain of this exception.
 *
 * @param T The type of exception to check for.
 * @return true if cause is in chain otherwise false.
 */
inline fun <reified T: Throwable> Throwable?.isCauseInChain() =
        ExceptionUtils.isCauseInChain<T>(this)

/**
 * Gets the specified exception type (cause) if it is in the chain of this exception.
 *
 * @param T The type of exception to check for.
 * @return The discovered exception, otherwise null.
 */
inline fun <reified T: Throwable> Throwable?.getCauseInChain() =
        ExceptionUtils.getCauseInChain<T>(this)