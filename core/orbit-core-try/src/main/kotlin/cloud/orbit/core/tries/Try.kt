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

import java.util.*

sealed class Try<T> {
    abstract fun isSuccess(): Boolean
    fun isFailure() = !isSuccess()

    operator fun invoke() = get()

    abstract fun get(): T

    fun toOptional(): Optional<T> = when(this) {
        is Success -> Optional.of(get())
        is Failure -> Optional.empty()
    }

    infix fun getOrElse(body: () -> T): T = when(this) {
        is Success -> get()
        is Failure -> body()
    }

    fun getOrNull() = when(this) {
        is Success -> get()
        is Failure -> null
    }

    infix fun <Z> flatMap(body: (T) -> Try<Z>) = when(this) {
        is Success -> try {
            body(get())
        } catch(t: Throwable) {
            Failure<Z>(t)
        }
        is Failure -> Failure<Z>(value)
    }

    infix fun <Z> map(body: (T) -> Z) = flatMap { Success(body(it)) }

    infix fun onSuccess(body: (T) -> Unit) = when(this) {
        is Success -> {
            if(isSuccess()) { body(get()) }
            this
        }
        is Failure -> this
    }

    infix fun onFailure(body: (Throwable) -> Unit) = when(this) {
        is Success -> this
        is Failure -> {
            body(value)
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
    }
}

data class Success<T>(internal val value: T): Try<T>() {
    override fun isSuccess() = true

    override fun get() = value
}

data class Failure<T>(internal val value: Throwable): Try<T>() {
    override fun isSuccess() = false

    override fun get() = throw value
}