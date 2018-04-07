/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util

import orbit.util.tries.Try
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TryTest {
    class TryTestException : RuntimeException("TryTestException")

    @Test
    fun testBasicSuccess() {
        val success = Try { "success" }
        Assertions.assertTrue(success.isSuccess)
        Assertions.assertEquals("success", success.get())
    }

    @Test
    fun testBasicFailure() {
        val fail = Try { throw TryTestException() }
        Assertions.assertTrue(fail.isFailure)
        Assertions.assertThrows(TryTestException::class.java, fail::get)
    }

    @Test
    fun testGetOrElse() {
        val success = Try { "get" }.getOrElse { "else" }
        Assertions.assertEquals("get", success)

        val fail = Try<String> { throw TryTestException() }.getOrElse { "else" }
        Assertions.assertEquals("else", fail)
    }

    @Test
    fun testOrNull() {
        val success = Try { "get" }.orNull()
        Assertions.assertEquals("get", success)

        val fail = Try<String> { throw TryTestException() }.orNull()
        Assertions.assertEquals(null, fail)
    }

    @Test
    fun testMap() {
        val square = Try { 5 }.map { it * it }
        Assertions.assertEquals(25, square.get())

        val failInTry = Try<Int> { throw TryTestException() }.map { it * it }
        Assertions.assertTrue(failInTry.isFailure)

        val failInMap = Try { 5 }.map { throw TryTestException() }
        Assertions.assertTrue(failInMap.isFailure)
    }

    @Test
    fun testFlatMap() {
        val square = Try { 5 }.flatMap { Try { it * it } }
        Assertions.assertEquals(25, square.get())

        val failInTry = Try<Int> { throw TryTestException() }.flatMap { Try { it * it } }
        Assertions.assertTrue(failInTry.isFailure)

        val failInMap = Try { 5 }.flatMap { Try { throw TryTestException() } }
        Assertions.assertTrue(failInMap.isFailure)
    }

    @Test
    fun testOnSuccess() {
        var didTrigger = false
        Try { 5 * 5 }.onSuccess { didTrigger = true }
        Assertions.assertTrue(didTrigger)
    }

    @Test
    fun testOnFailure() {
        var didTrigger = false
        Try { throw TryTestException() }.onFailure { didTrigger = true }
        Assertions.assertTrue(didTrigger)
    }

    @Test
    fun testEquality() {
        val firstSuccess = Try { 5 }
        val secondSuccess = Try { 5 }
        val thirdSuccess = Try { 10 }

        Assertions.assertEquals(firstSuccess, firstSuccess)
        Assertions.assertEquals(firstSuccess, secondSuccess)
        Assertions.assertNotEquals(firstSuccess, thirdSuccess)

        val commonException = TryTestException()
        val firstFail = Try { commonException }
        val secondFail = Try { commonException }
        val thirdFail = Try { TryTestException() }

        Assertions.assertEquals(firstFail, firstFail)
        Assertions.assertEquals(firstFail, secondFail)
        Assertions.assertNotEquals(firstFail, thirdFail)

        Assertions.assertNotEquals(firstSuccess, firstFail)
    }

    @Test
    fun testMaybeConversion() {
        val maybeSuccess = Try { "trySuccess" }.asMaybe()
        Assertions.assertTrue(maybeSuccess.isPresent)
        Assertions.assertEquals("trySuccess", maybeSuccess.get())

        val maybeFail = Try { throw TryTestException() }.asMaybe()
        Assertions.assertTrue(maybeFail.isEmpty)
    }

    @Test
    fun testOptionalConversion() {
        val optionalSuccess = Try { "trySuccess" }.asOptional()
        Assertions.assertTrue(optionalSuccess.isPresent)
        Assertions.assertEquals("trySuccess", optionalSuccess.get())

        val optionalFailure = Try { throw TryTestException() }.asOptional()
        Assertions.assertFalse(optionalFailure.isPresent)
    }
}