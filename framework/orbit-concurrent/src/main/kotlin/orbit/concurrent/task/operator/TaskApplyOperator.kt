/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.concurrent.job.JobManager
import orbit.concurrent.task.TaskContext
import orbit.util.tries.Try

internal class TaskApplyOperator<T>(jobManager: JobManager, body: () -> T): TaskOperator<T, T>() {
    init {
        val taskContext = TaskContext.current()

        jobManager.submit {
            taskContext?.push()
            onFulfilled(Try { body() })
            taskContext?.pop()
        }
    }

    override fun onFulfilled(result: Try<T>) {
        value = result
        taskCompletionContext = TaskContext.current()
        triggerListeners()
    }
}