/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.concurrent.task.Task
import orbit.concurrent.task.TaskContext
import orbit.util.tries.Try
import java.util.concurrent.atomic.AtomicBoolean

internal class TaskAnyOf(tasks: Iterable<Task<*>>): TaskForwarder<Unit>() {
    init {
        val completed = AtomicBoolean(false)
        val taskContext = TaskContext.current()

        tasks.forEach { task ->
            task.doAlways {
                if (completed.compareAndSet(false, true)) {
                    taskContext?.push()
                    onNext(Try.success(Unit))
                    taskContext?.pop()
                }
            }
        }
    }
}