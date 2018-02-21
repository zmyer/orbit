/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.misc

/**
 * Utilities for working with [String]s.
 */
object StringUtils {
    /**
     * Alias for [IdentityUtils.secureRandomString].
     * @see [IdentityUtils.secureRandomString].
     */
    @JvmOverloads
    @JvmStatic
    fun secureRandomString(numBits: Int = 128): String = IdentityUtils.secureRandomString(numBits)

    /**
     * Alias for [IdentityUtils.pseudoRandomString].
     * @see [IdentityUtils.pseudoRandomString].
     */
    @JvmOverloads
    @JvmStatic
    fun pseudoRandomString(numBits: Int = 128): String = IdentityUtils.pseudoRandomString(numBits)
}