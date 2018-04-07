/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.pipeline

import orbit.concurrent.flow.Publisher
import orbit.concurrent.job.JobManager
import orbit.concurrent.pipeline.operator.PipelineDoAlways
import orbit.concurrent.pipeline.operator.PipelineDoOnError
import orbit.concurrent.pipeline.operator.PipelineDoOnValue
import orbit.concurrent.pipeline.operator.PipelineFilter
import orbit.concurrent.pipeline.operator.PipelineFlatMap
import orbit.concurrent.pipeline.operator.PipelineMap
import orbit.concurrent.pipeline.impl.PipelineSink
import orbit.concurrent.pipeline.operator.PipelineRunOn
import orbit.concurrent.pipeline.operator.PipelineTaskFlatMap
import orbit.concurrent.task.Task
import orbit.util.tries.Try
import java.util.function.Consumer

abstract class Pipeline<S, T>: Publisher<T> {
    internal abstract fun onSink(item: Try<S>)

    fun sinkValue(item: S): Pipeline<S, T> {
        onSink(Try.success(item))
        return this
    }

    fun sinkError(item: Throwable): Pipeline<S, T> {
        onSink(Try.failed(item))
        return this
    }

    /**
     * Upon this [Pipeline] receiving any value, executes the given function and emits the result of the original
     * [Pipeline].
     *
     * @param body The function to run.
     * @return The new pipeline.
     */
    infix fun doAlways(body: (Try<T>) -> Unit): Pipeline<S, T> =
            PipelineDoAlways(this, body).also { this.subscribe(it) }
    infix fun doAlways(body: Consumer<Try<T>>): Pipeline<S, T> =
            doAlways({ body.accept(it) })
    infix fun doAlways(body: Runnable): Pipeline<S, T> =
            doAlways({ body.run() })

    /**
     * Upon this [Pipeline] receiving a successful value, executes the given function and emits the result of the
     * original [Pipeline].
     *
     * @param body The function to run.
     * @return The new pipeline.
     */
    infix fun doOnValue(body: (T) -> Unit): Pipeline<S, T> =
            PipelineDoOnValue(this, body).also { this.subscribe(it) }
    infix fun doOnValue(body: Consumer<T>): Pipeline<S, T> =
            doOnValue({ body.accept(it) })
    infix fun doOnValue(body: Runnable): Pipeline<S, T> =
            doOnValue({ body.run() })

    /**
     * Upon this [Pipeline] receiving an error value, executes the given function and emits the result of the
     * original [Pipeline].
     *
     * @param body The function to run.
     * @return The new pipeline.
     */
    infix fun doOnError(body: (Throwable) -> Unit): Pipeline<S, T> =
            PipelineDoOnError(this, body).also { this.subscribe(it) }
    infix fun doOnError(body: Consumer<Throwable>): Pipeline<S, T> =
            doOnError({ body.accept(it) })
    infix fun doOnError(body: Runnable): Pipeline<S, T> =
            doOnError({ body.run() })

    /**
     * Synchronously maps successful values received by this [Pipeline] to a new value and emits it.
     *
     * If the received value is a failed value the [Pipeline] is failed with the same [Throwable].
     *
     * @param body The mapping function.
     * @return The new [Pipeline].
     */
    infix fun <V> map(body: (T) -> V): Pipeline<S, V> =
            PipelineMap(this, body).also { this.subscribe(it) }

    /**
     * Asynchronously maps successful values received by this [Pipeline] to a new [Pipeline], flattens and emits it.
     *
     * If the received value is a failed value the [Pipeline] is failed with the same [Throwable].
     *
     * @param body The mapping function.
     * @return The new [Pipeline].
     */
    infix fun <V> flatMap(body: (T) -> Pipeline<T, V>): Pipeline<S, V> =
            PipelineFlatMap(this, body).also { this.subscribe(it) }
    infix fun <V> flatMap(mapper: Pipeline<T, V>): Pipeline<S, V> =
            flatMap({mapper})


    /**
     * Asynchronously maps successful values received by this [Pipeline] to a new [Task], flattens and emits it.
     *
     * If the received value is a failed value the [Pipeline] is failed with the same [Throwable].
     *
     * @param body The mapping function.
     * @return The new [Pipeline].
     */
    infix fun <V> flatMapTask(body: (T) -> Task<V>): Pipeline<S, V> =
            PipelineTaskFlatMap(this, body).also { this.subscribe(it) }
    infix fun <V> flatMapTask(mapper: Task<V>): Pipeline<S, V> =
            flatMapTask({mapper})

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
    infix fun filter(body: (T) -> Boolean): Pipeline<S, T> =
            PipelineFilter(this, body).also { this.subscribe(it) }

    /**
     * Each value received by this [Pipeline] will be emitted on the specified [JobManager].
     * Each value is emitted regardless of if it is successful or an error.
     *
     * @param jobManager The [JobManager] to emit values on.
     * @return The new pipeline.
     */
    fun runOn(jobManager: JobManager): Pipeline<S, T> =
            PipelineRunOn(this, jobManager).also { this.subscribe(it) }
    infix fun runOn(body: () -> JobManager): Pipeline<S, T> =
            runOn(body())

    companion object {
        /**
         * Creates a new [Pipeline].
         *
         * @return The new [Pipeline].
         */
        @JvmStatic
        fun <V> create(): Pipeline<V, V> = PipelineSink()
    }
}