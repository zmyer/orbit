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

package orbit.concurrent.pipeline

import orbit.concurrent.job.JobManager
import orbit.concurrent.pipeline.operator.PipelineDoAlwaysOperator
import orbit.concurrent.pipeline.operator.PipelineMapOperator
import orbit.concurrent.pipeline.operator.PipelineDoOnErrorOperator
import orbit.concurrent.pipeline.operator.PipelineDoOnValueOperator
import orbit.concurrent.pipeline.operator.PipelineOperator
import orbit.concurrent.pipeline.operator.PipelineRunOnOperator
import orbit.util.tries.Try
import java.util.concurrent.atomic.AtomicReference

abstract class Pipeline<S, T> {
    private class Sink<X>: Pipeline<X, X>()

    private val listeners = AtomicReference(listOf<PipelineOperator<S, T, *>>())

    private fun addListener(listener: PipelineOperator<S, T, *>) {
        do {
            val listenerList = listeners.get()
        } while(!listeners.compareAndSet(listenerList, listenerList + listener))
    }

    internal fun triggerListeners(value: Try<T>) {
        listeners.get().forEach {
            it.onNext(value)
        }
    }

    internal open fun onSink(value: Try<S>) {
        @Suppress("UNCHECKED_CAST")
        triggerListeners(value as Try<T>)
    }

    fun sinkValue(value: S) {
        onSink(Try.success(value))
    }

    fun sinkError(throwable: Throwable) {
        onSink(Try.failed(throwable))
    }

    infix fun doAlways(body: (Try<T>) -> Unit): Pipeline<S, T> =
            PipelineDoAlwaysOperator(this, body).apply { addListener(this) }

    infix fun doOnValue(body: (T) -> Unit): Pipeline<S, T> =
            PipelineDoOnValueOperator(this, body).apply { addListener(this) }

    infix fun doOnError(body: (Throwable) -> Unit): Pipeline<S, T> =
            PipelineDoOnErrorOperator(this, body).apply { addListener(this) }

    infix fun <V> map(body: (T) -> V): Pipeline<S, V> =
            PipelineMapOperator(this, body).apply { addListener(this) }

    fun runOn(jobManager: JobManager): Pipeline<S, T> =
            PipelineRunOnOperator(this, jobManager).apply { addListener(this) }

    infix fun runOn(body: () -> JobManager): Pipeline<S, T> = runOn(body())

    companion object {
        @JvmStatic
        fun <V> create(): Pipeline<V, V> = Sink()
    }
}