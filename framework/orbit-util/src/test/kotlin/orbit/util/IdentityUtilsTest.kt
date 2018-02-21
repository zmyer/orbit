/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util

import orbit.util.misc.IdentityUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class IdentityUtilsTest {
    @Test
    fun testSecureRandom() {
        val firstString = IdentityUtils.secureRandomString()
        val secondString = IdentityUtils.secureRandomString()
        Assertions.assertTrue(firstString.isNotEmpty())
        Assertions.assertTrue(secondString.isNotEmpty())
        Assertions.assertNotEquals(firstString, secondString)
    }

    @Test
    fun testPseudoRandom() {
        val firstString = IdentityUtils.pseudoRandomString()
        val secondString = IdentityUtils.pseudoRandomString()
        Assertions.assertTrue(firstString.isNotEmpty())
        Assertions.assertTrue(secondString.isNotEmpty())
        Assertions.assertNotEquals(firstString, secondString)
    }

    @Test
    fun testSequentialId() {
        val first = IdentityUtils.sequentialId()
        val second = IdentityUtils.sequentialId()
        Assertions.assertNotEquals(first, second)
    }
}