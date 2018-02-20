/*
 Copyright (C) 2018 Electronic Arts Inc.  All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1.  Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2.  Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
     its contributors may be used to endorse or promote products derived
     from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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