/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.concurrent.job.JobManager
import orbit.concurrent.task.TaskContext
import orbit.util.tries.Try

internal class TaskApply<T>(jobManager: JobManager, body: () -> T): TaskForwarder<T>() {
    init {
        val taskContext = TaskContext.current()

        jobManager.submit {
            taskContext?.push()
            onNext(Try { body() })
            taskContext?.pop()
        }
    }
}