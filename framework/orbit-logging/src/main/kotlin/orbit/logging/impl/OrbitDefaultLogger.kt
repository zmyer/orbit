/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.logging.impl

import orbit.logging.Logger

internal class OrbitDefaultLogger(logger: org.slf4j.Logger): Logger, org.slf4j.Logger by logger