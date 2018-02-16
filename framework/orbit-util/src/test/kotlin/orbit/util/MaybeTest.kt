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

import orbit.util.maybe.Maybe
import orbit.util.maybe.toMaybe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Optional

class MaybeTest {
    @Test
    fun testBasic() {
        val empty: Maybe<String> = Maybe.empty()
        Assertions.assertTrue(empty.isEmpty)
        Assertions.assertThrows(Throwable::class.java, { empty.get() })

        val value = Maybe.just("valueTest")
        Assertions.assertTrue(value.isPresent)
        Assertions.assertEquals("valueTest", value.get())
    }

    @Test
    fun testMap() {
        val squared = Maybe.just(5) map { it * it }
        Assertions.assertTrue(squared.isPresent)
        Assertions.assertEquals(25, squared.get())

        val emptyMaybe: Maybe<Int> = Maybe.empty()
        val mapEmpty = emptyMaybe map { it * it }
        Assertions.assertTrue(mapEmpty.isEmpty)
    }

    @Test
    fun testFlatMap() {
        val squared = Maybe.just(5) flatMap { Maybe.just(it * it) }
        Assertions.assertTrue(squared.isPresent)
        Assertions.assertEquals(25, squared.get())

        val emptyMaybe: Maybe<Int> = Maybe.empty()
        val mapEmpty = emptyMaybe flatMap { Maybe.just(it * it) }
        Assertions.assertTrue(mapEmpty.isEmpty)
    }

    @Test
    fun testOnNull() {
        val testRealVal = Maybe.just("testVal")
        Assertions.assertNotNull(testRealVal.orNull())

        val testEmptyVal = Maybe.empty()
        Assertions.assertNull(testEmptyVal.orNull())
    }

    @Test
    fun testOnSomething() {
        var didRun = false
        val testRealVal = Maybe.just("testVal")
        testRealVal onSomething { didRun = true }
        Assertions.assertTrue(didRun)

        didRun = false
        val testEmptyVal = Maybe.empty()
        testEmptyVal onSomething { didRun = true }
        Assertions.assertFalse(didRun)
    }

    @Test
    fun testOnNothing() {
        var didRun = false
        val testRealVal = Maybe.empty()
        testRealVal onNothing { didRun = true }
        Assertions.assertTrue(didRun)

        didRun = false
        val testEmptyVal = Maybe.just("something")
        testEmptyVal onNothing { didRun = true }
        Assertions.assertFalse(didRun)
    }

    @Test
    fun testOptionalConversion() {
        val maybeSomeOptional = Maybe.just("maybeSomeOptional").toOptional()
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

    @Test
    fun testEquality() {
        val firstSome = Maybe.just("match")
        val secondSome = Maybe.just("match")
        val thirdSome = Maybe.just("dontMatch")
        Assertions.assertEquals(firstSome, firstSome)
        Assertions.assertEquals(firstSome, secondSome)
        Assertions.assertNotEquals(firstSome, thirdSome)

        val firstEmpty = Maybe.empty()
        val secondEmpty = Maybe.empty()
        Assertions.assertEquals(firstEmpty, firstEmpty)
        Assertions.assertEquals(firstEmpty, secondEmpty)

        Assertions.assertNotEquals(firstEmpty, thirdSome)

    }
}