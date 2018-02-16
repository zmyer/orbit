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
    fun toOptional(): Optional<out T> = if(isEmpty) {
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
    infix fun <V> flatMap(body: (T) -> Maybe<V>): Maybe<V> = if(isEmpty) {
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
    infix fun <V> map(body: (T) -> V): Maybe<V> = if(isEmpty) {
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
    infix fun onSomething(body: (T) -> Unit): Maybe<T> {
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
    infix fun onNothing(body: () -> Unit): Maybe<T> {
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