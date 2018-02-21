/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.util.tries.Try

internal class TaskDoOnErrorOperator<T>(private val body: (Throwable) -> Unit): TaskOperator<T, T>() {
    override fun onFulfilled(result: Try<T>) {
        value = try {
            result.onFailure(body)
            result
        } catch(throwable: Throwable) {
            Try.failed(throwable)
        }
        triggerListeners()
    }
}