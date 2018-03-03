/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.net

import orbit.concurrent.task.Task
import orbit.logging.Loggers

class ClusterManager(
        private val config: ClusterConfiguration
) {
    private val logger = Loggers.getLogger<ClusterManager>()

    fun joinCluster(): Task<ClusterContext> = Task {
        logger.info("Joining Cluster (${config.clusterIdentity} ${config.nodeIdentity})...")

        val context = ClusterContext(config, this)

        context
    }
}