/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.runtime

import orbit.logging.Loggers
import orbit.net.NetConfiguration
import orbit.net.NetManager
import orbit.util.misc.VersionUtils
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.eagerSingleton
import org.kodein.di.erased.instance
import org.kodein.di.erased.instanceOrNull
import org.kodein.di.erased.singleton
import org.kodein.di.newInstance

class Stage private constructor(private val kodein: Kodein) {
    private val logger = Loggers.getLogger<Stage>()

    fun start() {
        logger.info("Starting Orbit Framework...")
        VersionUtils.logVersionInfo()




        val nm: NetManager by kodein.instance()
        nm.toString()
    }

    companion object {
        fun createStage(stageConfiguration: StageConfiguration): Stage {
            val kodein = Kodein {
                // Net
                bind<NetConfiguration>() with singleton { stageConfiguration.netConfiguration }
                bind<NetManager>() with singleton { NetManager(instance()) }

                // Stage
                bind<StageConfiguration>() with singleton { stageConfiguration }
            }

            val stage: Stage by kodein.newInstance { Stage(kodein) }
            return stage
        }
    }
}