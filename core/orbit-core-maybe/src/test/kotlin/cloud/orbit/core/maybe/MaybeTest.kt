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

package cloud.orbit.core.maybe

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class MaybeTest {
    @Test
    fun simpleTest() {
        val empty: Maybe<String> = Maybe.empty()
        Assertions.assertTrue(empty.isEmpty)
        Assertions.assertThrows(Throwable::class.java, { empty.get() })

        val value = Maybe.of("valueTest")
        Assertions.assertTrue(value.isPresent)
        Assertions.assertEquals("valueTest", value.get())
    }

    @Test
    fun mapTest() {
        val squared = Maybe.of(5) map { it * it }
        Assertions.assertTrue(squared.isPresent)
        Assertions.assertEquals(25, squared.get())

        val emptyMaybe: Maybe<Int> = Maybe.empty()
        val mapEmpty = emptyMaybe.map { it * it }
        Assertions.assertTrue(mapEmpty.isEmpty)
    }

    @Test
    fun flatMapTest() {
        val squared = Maybe.of(5) flatMap { Maybe.of(it * it) }
        Assertions.assertTrue(squared.isPresent)
        Assertions.assertEquals(25, squared.get())

        val emptyMaybe: Maybe<Int> = Maybe.empty()
        val mapEmpty = emptyMaybe.flatMap { Maybe.of(it * it) }
        Assertions.assertTrue(mapEmpty.isEmpty)
    }

    @Test
    fun orNullTest() {
        val testRealVal = Maybe.of("testVal")
        Assertions.assertNotNull(testRealVal.orNull())

        val testEmptyVal = Maybe.empty()
        Assertions.assertNull(testEmptyVal.orNull())
    }

    @Test
    fun optionalConversionTest() {
        val maybeSomeOptional = Maybe.of("maybeSomeOptional").toOptional()
        Assertions.assertTrue(maybeSomeOptional.isPresent)
        Assertions.assertEquals("maybeSomeOptional", maybeSomeOptional.get())

        val maybeNoneOptional = Maybe.empty().toOptional()
        Assertions.assertFalse(maybeNoneOptional.isPresent)

        val optionalSomeMaybe = Optional.of("optionalSomeMaybe").toMaybe()
        Assertions.assertTrue(optionalSomeMaybe.isPresent)
        Assertions.assertEquals("optionalSomeMaybe", optionalSomeMaybe.get())

        val optionalNoneMaybe = Optional.empty<String>().toMaybe()
        Assertions.assertTrue(optionalNoneMaybe.isEmpty)
    }
}