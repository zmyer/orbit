/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.pipeline.operator

import orbit.concurrent.pipeline.Pipeline
import orbit.util.tries.Try

internal class PipelineFilterOperator<S, I>(parent: Pipeline<S, I>, private val body: (I) -> Boolean):
        PipelineOperator<S, I, I>(parent) {

    override fun onNext(value: Try<I>) {
        value
            .onSuccess {
                try {
                    if(body(it)) {
                        triggerListeners(Try.success(it))
                    }
                } catch(throwable: Throwable) {
                    triggerListeners(Try.failed(throwable))
                }
            }
    }
}