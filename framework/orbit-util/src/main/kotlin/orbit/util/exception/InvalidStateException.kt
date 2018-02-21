/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.exception

/**
 * Exception thrown when a system is in an invalid state for the call.
 */
class InvalidStateException(message: String): OrbitException(message)