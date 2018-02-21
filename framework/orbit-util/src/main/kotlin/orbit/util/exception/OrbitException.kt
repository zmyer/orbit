/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.exception

/**
 * The base exception type used by Orbit.
 * All Orbit exceptions derive from this exception.
 */
open class OrbitException
    @JvmOverloads
    constructor(message: String? = null, cause: Throwable? = null):
        RuntimeException(message, cause)