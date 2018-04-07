/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.job.impl

import orbit.concurrent.job.JobManager
import orbit.concurrent.job.JobManagers
import orbit.util.misc.Disposable
import java.util.concurrent.ExecutorService

internal class JavaExecutorJobManager(private val executorService: ExecutorService) : JobManager {

    private class JobOffer(private val body: () -> Unit) : Disposable {

        @Volatile
        private var isCanceled = false

        fun run() {
            if (!isCanceled) {
                try {
                    body()
                } catch (t: Throwable) {
                    JobManagers.handleUncaughtException(t)
                }
            }
        }

        override fun dispose() {
            isCanceled = true
        }
    }

    override fun submit(body: () -> Unit): Disposable {
        val job = JobOffer(body)
        executorService.submit(Runnable {
            job.run()
        })
        return job
    }

    override fun dispose() {
        executorService.shutdown()
    }
}