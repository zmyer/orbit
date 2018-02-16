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

package orbit.util

import orbit.util.exception.ExceptionUtils
import orbit.util.exception.OrbitException
import orbit.util.exception.isCauseInChain
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ExceptionTest {
    @Test
    fun basicTest() {
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
    fun extensionsTest() {
        val blankRuntimeException = RuntimeException("runtimeException")
        Assertions.assertFalse(blankRuntimeException.isCauseInChain<OrbitException>())

        val orbitException = OrbitException("orbitException")
        Assertions.assertTrue(orbitException.isCauseInChain<OrbitException>())

        val orbitRuntimeException = RuntimeException("orbitRuntimeException", OrbitException())
        Assertions.assertTrue(orbitRuntimeException.isCauseInChain<OrbitException>())
    }

    @Test
    fun utilsTest() {
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