/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.pipeline.operator

import orbit.concurrent.pipeline.Pipeline
import orbit.concurrent.pipeline.impl.AbstractPipelineImpl
import orbit.util.tries.Try

internal abstract class PipelineOperator<S, T, R>(private val parent: Pipeline<S, T>) :
    AbstractPipelineImpl<S, T, R>() {
    override fun onSink(item: Try<S>) {
        parent.onSink(item)
    }
}