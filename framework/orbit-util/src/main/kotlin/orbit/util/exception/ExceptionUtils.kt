/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.exception

import orbit.util.maybe.Maybe

object ExceptionUtils {
    private const val MAX_DEPTH = 32

    private tailrec fun <T : Throwable> checkChainRecursive(cause: Class<out T>, chain: Throwable?, depth: Int = 0):
            Maybe<T> {
        return if (chain != null && depth < MAX_DEPTH) {
            if (cause.isInstance(chain)) {
                @Suppress("UNCHECKED_CAST")
                Maybe.just(chain as T)
            } else {
                checkChainRecursive(cause, chain.cause, depth + 1)
            }
        } else {
            Maybe.empty()
        }
    }

    /**
     * Checks whether the specified exception type (cause) is in the chain of exceptions provided.
     *
     * @param T The type of exception to check for.
     * @param chain The exception chain to check.
     * @return true if cause is in chain otherwise false.
     */
    inline fun <reified T : Throwable> isCauseInChain(chain: Throwable?) =
        isCauseInChain(T::class.java, chain)

    /**
     * Gets the specified exception type (cause) if it is in the chain of exceptions provided.
     *
     * @param T The type of exception to check for.
     * @param chain The exception chain to check.
     * @return The discovered exception, otherwise null.
     */
    inline fun <reified T : Throwable> getCauseInChain(chain: Throwable?) =
        getCauseInChain(T::class.java, chain)

    /**
     * Checks whether the specified exception type (cause) is in the chain of exceptions provided.
     *
     * @param cause The type of exception to check for.
     * @param chain The exception chain to check.
     * @return true if cause is in chain otherwise false.
     */
    @JvmStatic
    fun <T : Throwable> isCauseInChain(cause: Class<out T>, chain: T?) =
        checkChainRecursive(cause, chain).isPresent

    /**
     * Gets the specified exception type (cause) if it is in the chain of exceptions provided.
     *
     * @param cause The type of exception to check for.
     * @param chain The exception chain to check.
     * @return The discovered exception, otherwise null.
     */
    @JvmStatic
    fun <T : Throwable> getCauseInChain(cause: Class<out T>, chain: Throwable?): Maybe<T> =
        checkChainRecursive(cause, chain)

}