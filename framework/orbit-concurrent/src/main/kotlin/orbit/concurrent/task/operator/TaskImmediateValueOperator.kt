/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.concurrent.task.TaskContext
import orbit.util.tries.Try

internal class TaskImmediateValueOperator<T>(immediateValue: Try<T>): TaskOperator<T, T>() {
    init {
        onFulfilled(immediateValue)
    }

    override fun onFulfilled(result: Try<T>) {
        value = result
        taskCompletionContext = TaskContext.current()
        triggerListeners()
    }
}