/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.net

sealed class MessageTarget {
    object Broadcast: MessageTarget()
    object Anycast: MessageTarget()
    data class Unicast(val nodeIdentity: NodeIdentity): MessageTarget()
    data class Multicast(val nodes: Iterable<NodeIdentity>): MessageTarget() {
        constructor(vararg nodes: NodeIdentity): this(nodes.asIterable())
    }

}