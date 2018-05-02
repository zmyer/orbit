/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.misc

import orbit.logging.Loggers

/**
 * Utilities for determining versions.
 */
object VersionUtils {
    private val logger = Loggers.getLogger<VersionUtils>()
    /**
     * The codename for the current version of Orbit.
     */
    @JvmStatic
    val orbitCodename
        get() = javaClass.`package`.implementationTitle ?: "Orbit"

    /**
     * The current version of Orbit.
     */
    @JvmStatic
    val orbitVersion
        get() = javaClass.`package`.implementationVersion ?: "dev"

    /**
     * The current compatibility version of Orbit.
     */
    @JvmStatic
    val orbitCompatibilityVersion
        get() = javaClass.`package`.specificationVersion ?: "dev"

    /**
     * Prints information about the current environment to the logs.
     */
    @JvmStatic
    fun logVersionInfo() {
        logger.info("Orbit: ${VersionUtils.orbitVersion} (${VersionUtils.orbitCodename} " +
                "${VersionUtils.orbitCompatibilityVersion})")
        logger.info("JVM: ${System.getProperty("java.version")} " +
                "(${System.getProperty("java.vm.vendor")} " +
                "${System.getProperty("java.vm.name")} " +
                "${System.getProperty("java.vm.version")})")
        logger.info("Kotlin: ${KotlinVersion.CURRENT}")
    }

}