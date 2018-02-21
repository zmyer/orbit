/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.concurrent.task.Task
import orbit.util.tries.Failure
import orbit.util.tries.Try
import java.util.concurrent.atomic.AtomicInteger

internal class TaskAllOfOperator(tasks: Iterable<Task<*>>): TaskOperator<Unit, Unit>() {
    @Volatile
    private var resultHolder: Try<Unit> = Try.success(Unit)

    init {
        val countdown = AtomicInteger(tasks.count())

        tasks.forEach { task ->
            task.doAlways {
                when(it) {
                    is Failure -> resultHolder = Try.failed(it.getThrowable())
                }

                if (countdown.decrementAndGet() == 0) {
                    onFulfilled(resultHolder)
                }
            }
        }
    }

    override fun onFulfilled(result: Try<Unit>) {
        value = result
        triggerListeners()
    }
}