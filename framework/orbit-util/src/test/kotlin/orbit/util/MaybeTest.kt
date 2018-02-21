/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util

import orbit.util.maybe.Maybe
import orbit.util.maybe.asMaybe
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
        val squared = Maybe.just(5).map { it * it }
        Assertions.assertTrue(squared.isPresent)
        Assertions.assertEquals(25, squared.get())

        val emptyMaybe: Maybe<Int> = Maybe.empty()
        val mapEmpty = emptyMaybe.map { it * it }
        Assertions.assertTrue(mapEmpty.isEmpty)
    }

    @Test
    fun testFlatMap() {
        val squared = Maybe.just(5).flatMap { Maybe.just(it * it) }
        Assertions.assertTrue(squared.isPresent)
        Assertions.assertEquals(25, squared.get())

        val emptyMaybe: Maybe<Int> = Maybe.empty()
        val mapEmpty = emptyMaybe.flatMap { Maybe.just(it * it) }
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
        testRealVal.onSomething { didRun = true }
        Assertions.assertTrue(didRun)

        didRun = false
        val testEmptyVal = Maybe.empty()
        testEmptyVal.onSomething { didRun = true }
        Assertions.assertFalse(didRun)
    }

    @Test
    fun testOnNothing() {
        var didRun = false
        val testRealVal = Maybe.empty()
        testRealVal.onNothing { didRun = true }
        Assertions.assertTrue(didRun)

        didRun = false
        val testEmptyVal = Maybe.just("something")
        testEmptyVal.onNothing { didRun = true }
        Assertions.assertFalse(didRun)
    }

    @Test
    fun testOptionalConversion() {
        val maybeSomeOptional = Maybe.just("maybeSomeOptional").asOptional()
        Assertions.assertTrue(maybeSomeOptional.isPresent)
        Assertions.assertEquals("maybeSomeOptional", maybeSomeOptional.get())

        val maybeNoneOptional = Maybe.empty().asOptional()
        Assertions.assertFalse(maybeNoneOptional.isPresent)

        val optionalSomeMaybe = Optional.of("optionalSomeMaybe").asMaybe()
        Assertions.assertTrue(optionalSomeMaybe.isPresent)
        Assertions.assertEquals("optionalSomeMaybe", optionalSomeMaybe.get())

        val optionalNoneMaybe = Optional.empty<String>().asMaybe()
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