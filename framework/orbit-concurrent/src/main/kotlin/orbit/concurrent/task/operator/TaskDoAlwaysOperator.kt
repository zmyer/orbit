/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.util.tries.Try

internal class TaskDoAlwaysOperator<T>(private val body: (Try<T>) -> Unit): TaskOperator<T, T>() {
    override fun onFulfilled(result: Try<T>) {
        value = try {
            body(result)
            result
        } catch(throwable: Throwable) {
            Try.failed(throwable)
        }
        triggerListeners()
    }
}