/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.job.impl

import orbit.concurrent.job.JobManager
import orbit.concurrent.job.JobManagers
import orbit.util.misc.Disposable

internal class BlockingJobManager : JobManager {
    private object DummyDisposable : Disposable {
        override fun dispose() {

        }
    }

    override fun submit(body: () -> Unit): Disposable {
        try {
            body()
        } catch (throwable: Throwable) {
            JobManagers.handleUncaughtException(throwable)
        }
        return DummyDisposable
    }

    override fun dispose() {

    }
}