/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.pipeline.operator

import orbit.concurrent.pipeline.Pipeline
import orbit.util.tries.Try

internal class PipelineFlatMapOperator<S, I, O>(parent: Pipeline<S, I>, private val body: (I) -> Pipeline<I, O>):
        PipelineOperator<S, I, O>(parent) {

    override fun onNext(value: Try<I>) {
        value.onSuccess {
            try {
                val nestedPipeline = body(it)
                nestedPipeline.doAlways {
                    triggerListeners(it)
                }
                nestedPipeline.sinkValue(it)
            } catch(throwable: Throwable) {
                triggerListeners(Try.failed(throwable))
            }
        }.onFailure {
            triggerListeners(Try.failed(it))
        }
    }
}