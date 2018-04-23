/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.net

data class NetConfiguration(
    val clusterName: ClusterName = ClusterName("orbit-cluster"),
    val nodeName: NodeName = NodeName(clusterName.nameString),
    val nodeIdentity: NodeIdentity
)