/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.concurrent.task.Task
import orbit.util.tries.Try
import java.util.concurrent.atomic.AtomicBoolean

internal class TaskAnyOfOperator(tasks: Iterable<Task<*>>): TaskOperator<Unit, Unit>() {
    init {
        val completed = AtomicBoolean(false)

        tasks.forEach { task ->
            task.doAlways  {
                if(completed.compareAndSet(false, true))
                {
                    onFulfilled(Try.success(Unit))
                }
            }
        }
    }

    override fun onFulfilled(result: Try<Unit>) {
        value = result
        triggerListeners()
    }
}