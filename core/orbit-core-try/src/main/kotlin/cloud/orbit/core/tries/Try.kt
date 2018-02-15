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

package cloud.orbit.core.tries

import cloud.orbit.core.maybe.Maybe
import java.util.Optional

sealed class Try<T> {
    abstract val isSuccess: Boolean
    val isFailure get() = !isSuccess

    operator fun invoke() = get()

    abstract fun get(): T

    fun toOptional() = when(this) {
        is Success -> Optional.of(get())
        is Failure -> Optional.empty()
    }

    fun toMaybe() = when(this) {
        is Success -> Maybe.of(get())
        is Failure -> Maybe.empty()
    }

    infix fun getOrElse(body: () -> T): T = when(this) {
        is Success -> get()
        is Failure -> body()
    }

    fun orNull() = when(this) {
        is Success -> get()
        is Failure -> null
    }

    infix fun <Z> flatMap(body: (T) -> Try<Z>) = when(this) {
        is Success -> try {
            body(get())
        } catch(t: Throwable) {
            Failure<Z>(t)
        }
        is Failure -> Failure<Z>(throwable)
    }

    infix fun <Z> map(body: (T) -> Z) = flatMap { Success(body(it)) }

    infix fun onSuccess(body: (T) -> Unit) = when(this) {
        is Success -> {
            body(get())
            this
        }
        is Failure -> this
    }

    infix fun onFailure(body: (Throwable) -> Unit) = when(this) {
        is Success -> this
        is Failure -> {
            body(throwable)
            this
        }
    }

    companion object {
        operator fun <V> invoke(body: () -> V) = create(body)

        @JvmStatic
        fun <V> create(body: () -> V): Try<V> = try {
            Success(body())
        } catch(v: Throwable) {
            Failure(v)
        }

        @JvmStatic
        fun <V> success(value: V) = Success(value)

        @JvmStatic
        fun <V> failed(throwable: Throwable) = Failure<V>(throwable)
    }
}

data class Success<T>(private val value: T): Try<T>() {
    override val isSuccess = true

    override fun get() = value
}

data class Failure<T>(private val value: Throwable): Try<T>() {
    override val isSuccess = false

    override fun get() = throw throwable

    internal val throwable
        get() = value
}