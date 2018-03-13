/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.util.tries.Try

internal class TaskMap<I, O>(private val body: (I) -> O): TaskOperator<I, O>() {
    override fun operator(item: Try<I>) {
        item.onSuccess {
            publish(Try { body(it) })
        }.onFailure {
            publish(Try.failed(it))
        }
    }
}