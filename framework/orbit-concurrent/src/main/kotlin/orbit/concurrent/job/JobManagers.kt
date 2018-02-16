/*
 Copyright (C) 2018 Electronic Arts Inc.  All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1.  Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2.  Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
     its contributors may be used to endorse or promote products derived
     from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package orbit.concurrent.job

import orbit.concurrent.job.impl.BlockingJobManager
import orbit.concurrent.job.impl.JavaExecutorJobManager
import java.util.concurrent.Executors

/**
 * Helper methods for working with the default [JobManager]s and creating new ones.
 */
object JobManagers {
    private val parallel = newParallel()
    private val elastic = newElastic()
    private val blocking = newBlocking()

    internal fun handleUncaughtException(throwable: Throwable) {
        TODO("Log this properly")
    }

    /**
     * The default [JobManager] for parallel and CPU bound work.
     *
     * Maximum number of concurrent threads is equal to CPU cores.
     * This [JobManager] is not suitable for I/O bound work.
     *
     * @return The parallel [JobManager]
     */
    @JvmStatic
    fun parallel() = parallel

    /**
     * The default [JobManager] for elastic and I/O bound work.
     *
     * Threads are created on demand.
     *
     * @return The elastic [JobManager]
     */
    @JvmStatic
    fun elastic() = elastic

    /**
     * The default blocking [JobManager] for blocking work.
     *
     * All jobs submitted to this [JobManager] block the calling thread until completion.
     *
     * @return The blocking [JobManager]
     */
    @JvmStatic
    fun blocking() = blocking

    /**
     * Creates a new parallel [JobManager] with the specified level of parallelism.
     *
     * @param parallelism The desired level of parallelism. Defaults to number of CPU cores.
     * @return A new parallel [JobManager]
     */
    @JvmStatic
    @JvmOverloads
    fun newParallel(parallelism: Int = Runtime.getRuntime().availableProcessors()): JobManager {
        val javaService = Executors.newWorkStealingPool(parallelism)
        return JavaExecutorJobManager(javaService)
    }

    /**
     * Creates a new elastic [JobManager].
     *
     * @return A new elastic [JobManager]
     */
    @JvmStatic
    fun newElastic(): JobManager {
        val javaService = Executors.newCachedThreadPool()
        return JavaExecutorJobManager(javaService)
    }

    /**
     * Creates a new blocking [JobManager].
     *
     * @return A new blocking [JobManager]
     */
    @JvmStatic
    fun newBlocking(): JobManager = BlockingJobManager()

    /**
     * Creates a new single thread [JobManager].
     *
     * @return A new single thread [JobManager]
     */
    @JvmStatic
    fun newSingleThread(): JobManager {
        val javaService = Executors.newSingleThreadExecutor()
        return JavaExecutorJobManager(javaService)
    }


}