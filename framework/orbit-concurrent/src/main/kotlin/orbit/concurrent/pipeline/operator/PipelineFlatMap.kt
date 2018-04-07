/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.pipeline.operator

import orbit.concurrent.pipeline.Pipeline
import orbit.util.tries.Try

internal class PipelineFlatMap<S, T, R>(parent: Pipeline<S, T>, private val body: (T) -> Pipeline<T, R>) :
    PipelineOperator<S, T, R>(parent) {
    override fun operator(item: Try<T>) {
        item.onSuccess {
            try {
                val nestedPipeline = body(it)
                nestedPipeline.doAlways {
                    publish(it)
                }
                nestedPipeline.sinkValue(it)
            } catch (throwable: Throwable) {
                publish(Try.failed(throwable))
            }
        }.onFailure {
            publish(Try.failed(it))
        }
    }
}