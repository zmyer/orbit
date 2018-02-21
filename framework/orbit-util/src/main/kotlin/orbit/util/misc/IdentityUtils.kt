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

package orbit.util.misc

import orbit.util.exception.InvalidArgumentException
import java.security.SecureRandom
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicLong

/**
 * Utilities for generating identities.
 */
object IdentityUtils {
    private val base64Chars = charArrayOf(
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_')

    private val secureRNG = SecureRandom()
    private val pseudoRNG get() = ThreadLocalRandom.current()

    private val sequentialLongId = AtomicLong(0)

    private fun generateRandomString(numBits: Int, random: Random): String {
        tailrec fun internalGenerate(bitsLeft: Int, target: StringBuilder, random: Random) {
            val rangeMax = if(bitsLeft > 6) 64 else 1 shl bitsLeft
            target.append(base64Chars[random.nextInt(rangeMax)])
            val remaining = bitsLeft - 6
            if(remaining > 0) internalGenerate(remaining, target, random)
        }

        if(numBits <= 0) throw InvalidArgumentException("numBits must be > 0.")
        val targetString = StringBuilder(1 + (numBits / 6))
        internalGenerate(numBits, targetString, random)
        return targetString.toString()
    }

    /**
     * Generates a secure random string with the specified number of bits.
     *
     * This method uses SecureRandom and a 128-bit string generated with this is as unique as a UUID.
     *
     * @param numBits The number of bits of randomness.
     * @throws InvalidArgumentException if numBits is not > 0.
     * @return The secure random string.
     */
    @JvmOverloads
    @JvmStatic
    fun secureRandomString(numBits: Int = 128) = generateRandomString(numBits, secureRNG)

    /**
     * Generates a pseudo random string with the specified number of bits.
     *
     * This method should not be considered secure.
     *
     * @param numBits The number of bits of randomness.
     * @throws InvalidArgumentException if numBits is not > 0.
     * @return The secure random string.
     */
    @JvmOverloads
    @JvmStatic
    fun pseudoRandomString(numBits: Int = 128) = generateRandomString(numBits, pseudoRNG)

    /**
     * Generates a [Long] ID unique to this virtual machine.
     * @return The unique ID.
     */
    @JvmStatic
    fun sequentialId() = sequentialLongId.getAndIncrement()
}