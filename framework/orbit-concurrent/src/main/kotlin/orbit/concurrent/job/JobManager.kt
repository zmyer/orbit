/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.job

import orbit.util.misc.Disposable

/**
 * A manager to execute asynchronous jobs.
 */
interface JobManager : Disposable {
    /**
     * Submits a function to the underlying manager to be executed.
     *
     * @param body The function to be executed.
     * @return A disposable resource representing the job.
     */
    fun submit(body: () -> Unit): Disposable
}