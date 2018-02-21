/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.misc

/**
 * Utilities for determining versions.
 */
object VersionUtils {
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

}