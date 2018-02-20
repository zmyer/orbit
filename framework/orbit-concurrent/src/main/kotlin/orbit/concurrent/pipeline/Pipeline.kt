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
import orbit.concurrent.pipeline.operator.PipelineFilterOperator
import orbit.concurrent.pipeline.operator.PipelineFlatMapOperator
import orbit.concurrent.pipeline.operator.PipelineOperator
import orbit.concurrent.pipeline.operator.PipelineRunOnOperator
import orbit.concurrent.task.Task
import orbit.util.tries.Try
import java.util.concurrent.atomic.AtomicReference

/**
 * Represents a [Pipeline] which transforms sunk values.
 */
abstract class Pipeline<S, T> {
    private class Sink<X>: Pipeline<X, X>()

    private val listeners = AtomicReference(listOf<PipelineOperator<S, T, *>>())

    private fun addListener(listener: PipelineOperator<S, T, *>) {
        do {
            val listenerList = listeners.get()
        } while(!listeners.compareAndSet(listenerList, listenerList + listener))
    }

    @JvmSynthetic
    internal fun triggerListeners(value: Try<T>) {
        listeners.get().forEach {
            it.onNext(value)
        }
    }

    @JvmSynthetic
    internal open fun onSink(value: Try<S>) {
        @Suppress("UNCHECKED_CAST")
        triggerListeners(value as Try<T>)
    }

    /**
     * Sinks a successful value into the [Pipeline].
     *
     * @param value The value to sink.
     * @return The pipeline.
     */
    fun sinkValue(value: S): Pipeline<S, T> {
        onSink(Try.success(value))
        return this
    }

    /**
     * Sinks an error value into the [Pipeline].
     *
     * @param throwable The error to sink.
     * @return The pipeline.
     */
    fun sinkError(throwable: Throwable): Pipeline<S, T> {
        onSink(Try.failed(throwable))
        return this
    }

    /**
     * Upon this [Pipeline] receiving any value, executes the given function and emits the result of the original
     * [Pipeline].
     *
     * @param body The function to run.
     * @return The new pipeline.
     */
    fun doAlways(body: (Try<T>) -> Unit): Pipeline<S, T> =
            PipelineDoAlwaysOperator(this, body).apply { addListener(this) }

    /**
     * Upon this [Pipeline] receiving a successful value, executes the given function and emits the result of the
     * original [Pipeline].
     *
     * @param body The function to run.
     * @return The new pipeline.
     */
    fun doOnValue(body: (T) -> Unit): Pipeline<S, T> =
            PipelineDoOnValueOperator(this, body).apply { addListener(this) }

    /**
     * Upon this [Pipeline] receiving an error value, executes the given function and emits the result of the
     * original [Pipeline].
     *
     * @param body The function to run.
     * @return The new pipeline.
     */
    fun doOnError(body: (Throwable) -> Unit): Pipeline<S, T> =
            PipelineDoOnErrorOperator(this, body).apply { addListener(this) }

    /**
     * Synchronously maps successful values received by this [Pipeline] to a new value and emits it.
     *
     * If the received value is a failed value the [Pipeline] is failed with the same [Throwable].
     *
     * @param body The mapping function.
     * @return The new [Pipeline].
     */
    fun <V> map(body: (T) -> V): Pipeline<S, V> =
            PipelineMapOperator(this, body).apply { addListener(this) }

    /**
     * Asynchronously maps successful values received by this [Pipeline] to a new [Pipeline], flattens and emits it.
     *
     * If the received value is a failed value the [Pipeline] is failed with the same [Throwable].
     *
     * @param body The mapping function.
     * @return The new [Pipeline].
     */
    fun <V> flatMap(body: (T) -> Pipeline<T, V>): Pipeline<S, V> =
            PipelineFlatMapOperator(this, body).apply { addListener(this) }

    /**
     * Asynchronously maps successful values received by this [Pipeline] to a new [Pipeline], flattens and emits it.
     *
     * If the received value is a failed value the [Pipeline] is failed with the same [Throwable].
     *
     * @param mapper The pipeline to map into.
     * @return The new [Pipeline].
     */
    fun <V> flatMap(mapper: Pipeline<T, V>): Pipeline<S, V> =
            flatMap({mapper})

    /**
     * Evaluates each successful value received by this [Pipeline] against the supplied predicate and only emits those
     * which pass the test.
     *
     * If the received value is a failed value the new [Pipeline] is not triggered. If the mapping function
     * fails then the [Throwable] is emitted.
     *
     * @param body The predicate.
     * @return The filtered [Pipeline].
     */
    fun filter(body: (T) -> Boolean): Pipeline<S, T> =
            PipelineFilterOperator(this, body).apply { addListener(this) }

    /**
     * Each value received by this [Pipeline] will be emitted on the specified [JobManager].
     * Each value is emitted regardless of if it is successful or an error.
     *
     * @param jobManager The [JobManager] to emit values on.
     * @return The new pipeline.
     */
    fun runOn(jobManager: JobManager): Pipeline<S, T> =
            PipelineRunOnOperator(this, jobManager).apply { addListener(this) }

    /**
     * Each value received by this [Pipeline] will be emitted on the specified [JobManager].
     * Each value is emitted regardless of if it is successful or an error.
     *
     * @param body A function that returns the [JobManager] to emit values on.
     * @return The new pipeline.
     */
    fun runOn(body: () -> JobManager): Pipeline<S, T> = runOn(body())

    companion object {
        @JvmStatic
        fun <V> create(): Pipeline<V, V> = Sink()
    }
}