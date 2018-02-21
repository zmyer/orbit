/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.util.tries.Try

internal class TaskMapOperator<I, O>(private val body: (I) -> O): TaskOperator<I, O>() {
    override fun onFulfilled(result: Try<I>) {
        result.onSuccess {
            value = Try { body(it) }
        }.onFailure {
            value = Try.failed(it)
        }
        triggerListeners()
    }
}