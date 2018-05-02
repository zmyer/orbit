/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.runtime

import orbit.net.NetConfiguration

data class StageConfiguration(
    val netConfiguration: NetConfiguration = NetConfiguration()
)