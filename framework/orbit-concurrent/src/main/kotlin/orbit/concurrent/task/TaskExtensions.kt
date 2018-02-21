/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task

import java.util.concurrent.CompletableFuture

/**
 * Creates a [Task] which is completed based on the result of a [CompletableFuture].
 *
 * @return The new [Task].
 */
fun <T> CompletableFuture<T>.asTask(): Task<T> = Task.fromCompletableFuture(this)