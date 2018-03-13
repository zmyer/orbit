/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.concurrent.task.Task
import orbit.util.tries.Try

internal class TaskFlatMap<I, O>(private val body: (I) -> Task<O>): TaskOperator<I, O>() {
    override fun operator(item: Try<I>) {
        item.onSuccess {
            try {
                body(it).doAlways {
                    publish(it)
                }
            } catch(t: Throwable) {
                publish(Try.failed(t))
            }
        }.onFailure {
            publish(Try.failed(it))
        }
    }
}