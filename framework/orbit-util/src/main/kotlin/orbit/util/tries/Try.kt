/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.tries

import orbit.util.maybe.Maybe
import java.util.Optional
import java.util.function.Consumer

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
    fun asOptional() = when(this) {
        is Success -> Optional.of(get())
        is Failure -> Optional.empty()
    }

    /**
     * Converts the [Try] to an Orbit [Maybe].
     *
     * @return The Orbit [Maybe].
     */
    fun asMaybe() = when(this) {
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
    fun getOrElse(body: () -> T): T = when(this) {
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
    fun <Z> flatMap(body: (T) -> Try<Z>) = when(this) {
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
    fun <Z> map(body: (T) -> Z) = flatMap { Success(body(it)) }

    /**
     * Executes the function only if the [Try] is a computed value.
     * Returns the original [Try] for further operations.
     *
     * @param body The function to execute if a computer value.
     * @return The current [Try].
     */
    fun onSuccess(body: (T) -> Unit) = when(this) {
        is Success -> {
            body(get())
            this
        }
        is Failure -> this
    }
    fun onSuccess(body: Consumer<T>) =
            onSuccess({ body.accept(it)})
    fun onSuccess(body: Runnable) =
            onSuccess({ body.run()})

    /**
     * Executes the function only if the [Try] is an exception.
     * Returns the original [Try] for further operations.
     *
     * @param body The function to execute if an exception.
     * @return The current [Try].
     */
    fun onFailure(body: (Throwable) -> Unit) = when(this) {
        is Success -> this
        is Failure -> {
            body(throwable)
            this
        }
    }
    fun onFailure(body: Consumer<Throwable>) =
            onFailure({ body.accept(it)})
    fun onFailure(body: Runnable) =
            onFailure({ body.run()})

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

    fun getThrowable(): Throwable = throwable

    internal val throwable
        get() = value
}