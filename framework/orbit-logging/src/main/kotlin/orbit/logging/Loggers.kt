/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.logging

import orbit.logging.impl.OrbitDefaultLogger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * [Logger] factory used for providing loggers to other Orbit systems.
 */
object Loggers {
    /**
     * Creates a [Logger] with the specified name.
     * @param logName The name.
     * @return The new [Logger].
     */
    @JvmStatic
    fun getLogger(logName: String): Logger = OrbitDefaultLogger(LoggerFactory.getLogger(logName))

    /**
     * Creates a [Logger] for the specified [Class].
     * @param clazz The [Class].
     * @return The new [Logger].
     */
    @JvmStatic
    fun getLogger(clazz: Class<*>): Logger = OrbitDefaultLogger(LoggerFactory.getLogger(clazz))

    /**
     * Creates a [Logger] for the specified [KClass].
     * @param kClazz The [KClass].
     * @return The new [Logger].
     */
    fun getLogger(kClazz: KClass<*>): Logger = getLogger(kClazz.java)

    /**
     * Creates a [Logger] for the specified type.
     * @param T The type.
     * @return The new [Logger].
     */
    inline fun <reified T> getLogger(): Logger = getLogger(T::class)
}