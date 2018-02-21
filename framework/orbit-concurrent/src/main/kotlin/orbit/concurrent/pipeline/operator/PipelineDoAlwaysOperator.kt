/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.pipeline.operator

import orbit.concurrent.pipeline.Pipeline
import orbit.util.tries.Try

internal class PipelineDoAlwaysOperator<S, T>(parent: Pipeline<S, T>, private val body: (Try<T>) -> Unit):
        PipelineOperator<S, T, T>(parent) {

    override fun onNext(value: Try<T>) {
        try {
            body(value)
            triggerListeners(value)
        } catch(throwable: Throwable) {
            triggerListeners(Try.failed(throwable))
        }

    }
}