/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.misc

/**
 * Alias for [IdentityUtils.secureRandomString].
 * @see [IdentityUtils.secureRandomString].
 */
fun String.Companion.secureRandom(numBits: Int = 128) = StringUtils.secureRandomString(numBits)

/**
 * Alias for [IdentityUtils.pseudoRandomString].
 * @see [IdentityUtils.pseudoRandomString].
 */
fun String.Companion.pseudoRandom(numBits: Int = 128) = StringUtils.pseudoRandomString(numBits)