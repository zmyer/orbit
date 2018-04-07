/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.exception


/**
 * A generic unchecked exception.
 */
open class UncheckedException
@JvmOverloads
constructor(message: String? = null, cause: Throwable? = null):
        OrbitException(message, cause)