/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.concurrent.task.TaskContext
import orbit.util.tries.Try
import java.util.concurrent.CompletableFuture

internal class TaskFromCompletableFuture<T>(completableFuture: CompletableFuture<T>): TaskNoOp<T>() {
    init {
        val taskContext = TaskContext.current()

        completableFuture.handle { v, t ->
            taskContext?.push()

            if (t != null) {
                onNext(Try.failed(t))
            } else {
                onNext(Try.success(v!!))
            }

            taskContext?.pop()
        }
    }
}