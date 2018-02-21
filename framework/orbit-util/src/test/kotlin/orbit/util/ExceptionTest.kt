/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util

import orbit.util.exception.ExceptionUtils
import orbit.util.exception.OrbitException
import orbit.util.exception.isCauseInChain
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ExceptionTest {
    @Test
    fun testBasic() {
        val blankException = OrbitException()
        Assertions.assertNull(blankException.message)
        Assertions.assertNull(blankException.cause)

        val textException = OrbitException("textException")
        Assertions.assertEquals("textException", textException.message)
        Assertions.assertNull(textException.cause)

        val causedException = OrbitException("causedException", RuntimeException())
        Assertions.assertEquals("causedException", causedException.message)
        Assertions.assertNotNull(causedException.cause)
    }

    @Test
    fun testExtensions() {
        val blankRuntimeException = RuntimeException("runtimeException")
        Assertions.assertFalse(blankRuntimeException.isCauseInChain<OrbitException>())

        val orbitException = OrbitException("orbitException")
        Assertions.assertTrue(orbitException.isCauseInChain<OrbitException>())

        val orbitRuntimeException = RuntimeException("orbitRuntimeException", OrbitException())
        Assertions.assertTrue(orbitRuntimeException.isCauseInChain<OrbitException>())
    }

    @Test
    fun testUtils() {
        val nullException: OrbitException? = null
        Assertions.assertFalse(ExceptionUtils.isCauseInChain<OrbitException>(nullException))
        Assertions.assertNull(ExceptionUtils.getCauseInChain<OrbitException>(nullException))

        val topLevelException = OrbitException()
        Assertions.assertTrue(ExceptionUtils.isCauseInChain<OrbitException>(topLevelException))
        Assertions.assertNotNull(ExceptionUtils.getCauseInChain<OrbitException>(topLevelException))

        val secondLevelException = RuntimeException("secondLevel", OrbitException())
        Assertions.assertTrue(ExceptionUtils.isCauseInChain<OrbitException>(secondLevelException))
        Assertions.assertNotNull(ExceptionUtils.getCauseInChain<OrbitException>(secondLevelException))
    }
}