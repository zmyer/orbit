/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.runtime

import orbit.concurrent.task.Task
import orbit.logging.Loggers
import orbit.util.misc.VersionUtils

class Stage(
        private val config: StageConfiguration
) {
    private val logger = Loggers.getLogger<Stage>()

    fun start(): Task<Unit> = Task {
        logger.info ("Starting ${VersionUtils.orbitCodename} ${VersionUtils.orbitVersion}...")
    }
}