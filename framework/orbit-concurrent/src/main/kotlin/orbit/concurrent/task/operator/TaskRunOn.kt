/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.concurrent.flow.Subscriber
import orbit.concurrent.job.JobManager
import orbit.util.tries.Try

internal class TaskRunOn<T>(private val jobManager: JobManager): TaskNoOp<T>() {
    override fun sendTo(listener: Subscriber<T>, opVal: Try<T>) {
        jobManager.submit {
            super.sendTo(listener, opVal)
        }
    }
}