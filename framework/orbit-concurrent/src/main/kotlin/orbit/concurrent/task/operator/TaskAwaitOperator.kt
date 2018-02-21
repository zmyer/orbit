/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.util.tries.Try
import java.util.concurrent.CountDownLatch

internal class TaskAwaitOperator<I>: TaskOperator<I, I>() {
    private val latch = CountDownLatch(1)

    override fun onFulfilled(result: Try<I>) {
        value = result
        latch.countDown()
        triggerListeners()
    }

    internal fun waitOnLatch(): I {
        latch.await()
        return value!!.get()
    }
}