/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.net

import orbit.util.misc.secureRandom

data class ClusterConfiguration(
        val clusterIdentity: ClusterIdentity = ClusterIdentity("orbit-cluster"),
        val nodeIdentity: NodeIdentity = NodeIdentity(String.secureRandom())
)