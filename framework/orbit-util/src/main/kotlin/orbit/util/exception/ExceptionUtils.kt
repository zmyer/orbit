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

package orbit.util.exception

object ExceptionUtils {
    private const val MAX_DEPTH = 128

    private tailrec fun <T: Throwable> checkChainRecursive(cause: Class<out T>, chain: Throwable?, depth: Int = 0): T? {
        return if (chain != null && depth < MAX_DEPTH) {
            if (cause.isInstance(chain)) {
                @Suppress("UNCHECKED_CAST")
                chain as? T
            } else {
                checkChainRecursive(cause, chain.cause, depth + 1)
            }
        } else {
            null
        }
    }

    /**
     * Checks whether the specified exception type (cause) is in the chain of exceptions provided.
     *
     * @param T The type of exception to check for.
     * @param chain The exception chain to check.
     * @return true if cause is in chain otherwise false.
     */
    inline fun <reified T: Throwable> isCauseInChain(chain: Throwable?) =
            isCauseInChain(T::class.java, chain)

    /**
     * Gets the specified exception type (cause) if it is in the chain of exceptions provided.
     *
     * @param T The type of exception to check for.
     * @param chain The exception chain to check.
     * @return The discovered exception, otherwise null.
     */
    inline fun <reified T: Throwable> getCauseInChain(chain: Throwable?) =
            getCauseInChain(T::class.java, chain)

    /**
     * Checks whether the specified exception type (cause) is in the chain of exceptions provided.
     *
     * @param cause The type of exception to check for.
     * @param chain The exception chain to check.
     * @return true if cause is in chain otherwise false.
     */
    @JvmStatic
    fun <T: Throwable> isCauseInChain(cause: Class<out T>, chain: T?) =
            checkChainRecursive(cause, chain) != null

    /**
     * Gets the specified exception type (cause) if it is in the chain of exceptions provided.
     *
     * @param cause The type of exception to check for.
     * @param chain The exception chain to check.
     * @return The discovered exception, otherwise null.
     */
    @JvmStatic
    fun <T: Throwable> getCauseInChain(cause: Class<out T>, chain: Throwable?): T? =
            checkChainRecursive(cause, chain)

}