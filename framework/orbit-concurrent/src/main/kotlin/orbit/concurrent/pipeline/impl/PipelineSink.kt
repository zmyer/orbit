/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.pipeline.impl

import orbit.util.tries.Try

internal class PipelineSink<S, T>: AbstractPipelineImpl<S, T, T>() {
    override fun operator(item: Try<T>) {
        publish(item)
    }
}