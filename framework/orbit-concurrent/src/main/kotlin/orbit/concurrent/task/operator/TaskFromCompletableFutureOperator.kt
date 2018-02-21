/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.util.tries.Try
import java.util.concurrent.CompletableFuture


internal class TaskFromCompletableFutureOperator<T>(completableFuture: CompletableFuture<T>): TaskOperator<T, T>() {
    init {
        completableFuture.handle { v, t ->
            if (t != null) {
                onFulfilled(Try.failed(t))
            } else {
                onFulfilled(Try.success(v!!))
            }
        }
    }

    override fun onFulfilled(result: Try<T>) {
        value = result
        triggerListeners()
    }
}