/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.maybe

import java.util.Optional

/**
 * Represents the result of a computation which is either a computed value ([Some]) or nothing ([None]).
 */
sealed class Maybe<out T> {
    abstract val isEmpty: Boolean
    val isPresent get() = !isEmpty

    abstract fun get(): T

    /**
     * Converts the [Maybe] to a Java [Optional].
     */
    fun asOptional(): Optional<out T> = if(isEmpty) {
        Optional.empty()
    } else {
        Optional.of(get())
    }

    /**
     * Returns the computed value if present or null if nothing.
     *
     * @return The computed value or null.
     */
    fun orNull(): T? = if(isEmpty) {
        null
    } else {
        get()
    }

    /**
     * Transforms the computed value of the [Maybe] to another [Maybe] and flattens it.
     *
     * If the initial [Maybe] was nothing the new maybe will also be nothing.
     *
     * @param body The mapping function.
     * @return The new [Maybe].
     */
    fun <V> flatMap(body: (T) -> Maybe<V>): Maybe<V> = if(isEmpty) {
        None
    } else {
        body(get())
    }

    /**
     * Transforms the result of the computed value and returns it.
     *
     * If the initial [Maybe] was nothing the new maybe will also be nothing.
     *
     * @param body The mapping function.
     * @return The new [Maybe].
     */
    fun <V> map(body: (T) -> V): Maybe<V> = if(isEmpty) {
        None
    } else {
        Some(body(get()))
    }

    /**
     * Executes the supplied function if the [Maybe] contains a computed value and not nothing.
     *
     * @param body The function to execute.
     * @return The original [Maybe] for chaining purposes.
     */
    fun onSomething(body: (T) -> Unit): Maybe<T> {
        if(isPresent) {
            body(get())
        }
        return this
    }

    /**
     * Executes the supplied function if the [Maybe] contains nothing.
     *
     * @param body The function to execute.
     * @return The original [Maybe] for chaining purposes.
     */
    fun onNothing(body: () -> Unit): Maybe<T> {
        if(isEmpty) {
            body()
        }
        return this
    }

    companion object {
        /**
         * Return an empty nothing ([None]) [Maybe].
         *
         * @return The [Maybe].
         */
        @JvmStatic
        fun empty() = None

        /**
         * Return a [Maybe] with the provided computed value ([Some]).
         *
         * @return The [Maybe].
         */
        @JvmStatic
        fun <V> just(value: V) = Some(value)

        /**
         * Creates an Orbit [Maybe] from the provided Java [Optional].
         *
         * @param optional The Java [Optional].
         * @return The [Maybe].
         */
        @JvmStatic
        fun <V> fromOptional(optional: Optional<V>) = if(optional.isPresent) {
            just(optional.get())
        } else {
            empty()
        }
    }
}


object None: Maybe<Nothing>() {
    override val isEmpty = true
    override fun get() = throw IllegalAccessException("Trying to use None.get")
}

data class Some<T>(private val value: T): Maybe<T>() {
    override val isEmpty = false
    override fun get() = value
}