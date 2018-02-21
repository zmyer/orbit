/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent

import orbit.concurrent.job.JobManagers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

class JobManagerTest {
    @Test
    fun testBlocking() {
        val executor = JobManagers.blocking()
        val notSet = AtomicBoolean(false)
        executor.submit {
            Thread.sleep(100)
            notSet.set(true)
        }
        Assertions.assertTrue(notSet.get())
    }

    @Test
    fun testCancel() {
        val countDownLatch = CountDownLatch(1)
        val executor = JobManagers.newSingleThread()
        val notSet = AtomicBoolean(false)

        executor.submit {
            Thread.sleep(100)
            countDownLatch.countDown()
        }
        val secondTask = executor.submit { notSet.set(true) }
        secondTask.dispose()

        countDownLatch.await()

        Assertions.assertFalse(notSet.get())
    }
}