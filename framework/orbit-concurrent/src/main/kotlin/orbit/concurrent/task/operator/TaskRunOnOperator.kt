/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.concurrent.job.JobManager
import orbit.util.tries.Try

internal class TaskRunOnOperator<T>(private val jobManager: JobManager): TaskOperator<T, T>() {
    override fun onFulfilled(result: Try<T>) {
        value = result
        triggerListeners()
    }

    override fun executeListener(listener: TaskOperator<T, *>, triggerVal: Try<T>) {
        jobManager.submit {
            super.executeListener(listener, triggerVal)
        }
    }
}