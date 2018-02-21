/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util.maybe

import java.util.Optional

/**
 * Converts any value to a [Maybe]. If null the [Maybe] will be nothing otherwise it will have a computed value.
 *
 * @return The [Maybe].
 */
fun <T> T?.asMaybe() = if(this == null) {
    Maybe.empty()
} else {
    Maybe.just(this)
}

/**
 * Converts a Java [Optional] to an Orbit [Maybe].
 *
 * @return The [Maybe].
 */
fun <T> Optional<T>.asMaybe() = Maybe.fromOptional(this)