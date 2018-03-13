/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.util.tries.Try
import java.util.concurrent.CountDownLatch

internal class TaskAwait<T>: TaskOperator<T, T>() {
    private var earlyValue: Try<T>? = null
    private val latch = CountDownLatch(1)

    override fun operator(item: Try<T>) {
        earlyValue = item
        latch.countDown()
        publish(item)
    }

    internal fun waitOnLatch(): T {
        latch.await()
        return earlyValue!!.get()
    }
}