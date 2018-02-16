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

package orbit.util.tries

import orbit.util.maybe.Maybe
import java.util.Optional

/**
 * Represents a computation that may be either a computed value ([Success]) or an exception ([Failure]).
 */
sealed class Try<T> {
    abstract val isSuccess: Boolean
    val isFailure get() = !isSuccess

    /**
     * Gets the computed value or throws the contained exception.
     *
     * @return THe computed value if applicable
     * @throws Throwable The contained exception if applicable
     */
    operator fun invoke() = get()

    /**
     * Gets the computed value or throws the contained exception.
     *
     * @return THe computed value if applicable
     * @throws Throwable The contained exception if applicable
     */
    abstract fun get(): T

    /**
     * Converts the [Try] to a Java [Optional].
     *
     * @return The Java [Optional].
     */
    fun toOptional() = when(this) {
        is Success -> Optional.of(get())
        is Failure -> Optional.empty()
    }

    /**
     * Converts the [Try] to an Orbit [Maybe].
     *
     * @return The Orbit [Maybe].
     */
    fun toMaybe() = when(this) {
        is Success -> Maybe.just(get())
        is Failure -> Maybe.empty()
    }

    /**
     * When a computed value is contained, return that value.
     * When an exception is contained return the result of the computed function.
     *
     * @param body The computation to perform if the [Try] is an exception.
     * @return The computed value or the result of the provided function.
     */
    infix fun getOrElse(body: () -> T): T = when(this) {
        is Success -> get()
        is Failure -> body()
    }

    /**
     * When a computed value is contained, return that value.
     * When an exception is contained, return null.
     *
     * @return The computed value or null.
     */
    fun orNull() = when(this) {
        is Success -> get()
        is Failure -> null
    }

    /**
     * Transforms the computed value to another [Try] and flattens it.
     *
     * If the original [Try] is an exception the new [Try] is failed with the same [Throwable].
     *
     * @param body The mapping function.
     * @return The new [Try].
     */
    infix fun <Z> flatMap(body: (T) -> Try<Z>) = when(this) {
        is Success -> try {
            body(get())
        } catch(t: Throwable) {
            Failure<Z>(t)
        }
        is Failure -> Failure<Z>(throwable)
    }

    /**
     * Transforms the computed value to another value and returns it.
     *
     * If the original [Try] is an exception the new [Try] is failed with the same [Throwable].
     *
     * @param body The mapping function.
     * @return The new [Try].
     */
    infix fun <Z> map(body: (T) -> Z) = flatMap { Success(body(it)) }

    /**
     * Executes the function only if the [Try] is a computed value.
     * Returns the original [Try] for further operations.
     *
     * @param body The function to execute if a computer value.
     * @return The current [Try].
     */
    infix fun onSuccess(body: (T) -> Unit) = when(this) {
        is Success -> {
            body(get())
            this
        }
        is Failure -> this
    }

    /**
     * Executes the function only if the [Try] is an exception.
     * Returns the original [Try] for further operations.
     *
     * @param body The function to execute if an exception.
     * @return The current [Try].
     */
    infix fun onFailure(body: (Throwable) -> Unit) = when(this) {
        is Success -> this
        is Failure -> {
            body(throwable)
            this
        }
    }

    companion object {
        /**
         * Creates a [Try] by executing the supplied function. The [Try] result is set to the computed value if the
         * function completed successfully or the exception if it throws.
         *
         * @param body The function to create the new [Try]
         * @return The new [Try].
         */
        operator fun <V> invoke(body: () -> V) = create(body)

        /**
         * Creates a [Try] by executing the supplied function. The [Try] result is set to the computed value if the
         * function completed successfully or the exception if it throws.
         *
         * @param body The function to create the new [Try]
         * @return The new [Try].
         */
        @JvmStatic
        fun <V> create(body: () -> V): Try<V> = try {
            Success(body())
        } catch(v: Throwable) {
            Failure(v)
        }

        /**
         * Creates a new computed value Try with the provided value.
         *
         * @param value The value to set to [Try] to.
         * @return The new [Try].
         */
        @JvmStatic
        fun <V> success(value: V) = Success(value)

        /**
         * Creates a new computed value Try with the provided exception.
         *
         * @param throwable The exception to set to [Try] to.
         * @return The new [Try].
         */
        @JvmStatic
        fun <V> failed(throwable: Throwable) = Failure<V>(throwable)
    }
}

/**
 * Represents a successfully completed computed value [Try].
 */
data class Success<T>(private val value: T): Try<T>() {
    override val isSuccess = true

    override fun get() = value
}

/**
* Represents a failed [Try].
*/
data class Failure<T>(private val value: Throwable): Try<T>() {
    override val isSuccess = false

    override fun get() = throw throwable

    internal val throwable
        get() = value
}