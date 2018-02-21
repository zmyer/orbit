/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.pipeline.operator

import orbit.concurrent.pipeline.Pipeline
import orbit.util.tries.Try

abstract class PipelineOperator<S, I, O>(private val parent: Pipeline<S, I>): Pipeline<S, O>() {
    internal abstract fun onNext(value: Try<I>)

    override fun onSink(value: Try<S>) {
        parent.onSink(value)
    }
}