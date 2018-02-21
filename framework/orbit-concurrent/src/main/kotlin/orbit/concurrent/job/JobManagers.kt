/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.job

import orbit.concurrent.job.impl.BlockingJobManager
import orbit.concurrent.job.impl.JavaExecutorJobManager
import orbit.logging.Loggers
import java.util.concurrent.Executors

/**
 * Helper methods for working with the default [JobManager]s and creating new ones.
 */
object JobManagers {
    private val logger = Loggers.getLogger<JobManagers>()

    private val parallel = newParallel()
    private val elastic = newElastic()
    private val blocking = newBlocking()


    internal fun handleUncaughtException(throwable: Throwable) {
        logger.error("Uncaught exception in JobManager.", throwable)
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