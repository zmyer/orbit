/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.pipeline.operator

import orbit.concurrent.pipeline.Pipeline
import orbit.util.tries.Try

internal class PipelineMapOperator<S, I, O>(parent: Pipeline<S, I>, private val body: (I) -> O):
        PipelineOperator<S, I, O>(parent) {

    override fun onNext(value: Try<I>) {
        value.onSuccess {
            triggerListeners(Try { body(it) })
        }.onFailure {
            triggerListeners(Try.failed(it))
        }
    }
}