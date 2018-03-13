/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent

import orbit.concurrent.job.JobManagers
import orbit.concurrent.pipeline.Pipeline
import orbit.concurrent.task.Task
import orbit.util.tries.Try
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger

class PipelineTest {
    @Test
    fun testDoAlways() {
        val counter = AtomicInteger(0)

        val pipeline = Pipeline.create<Int>()
            .doAlways {
                counter.incrementAndGet()
            }

        pipeline.sinkError(TestException())
        pipeline.sinkValue(5)
        Assertions.assertEquals(2, counter.get())
    }

    @Test
    fun testDoOnValue() {
        val counter = AtomicInteger(0)

        val pipeline = Pipeline.create<Int>()
            .doOnValue {
                counter.incrementAndGet()
            }

        pipeline.sinkError(TestException())
        pipeline.sinkValue(5)
        Assertions.assertEquals(1, counter.get())
    }

    @Test
    fun testDoOnError() {
        val counter = AtomicInteger(0)

        val pipeline = Pipeline.create<Int>()
            .doOnError {
                counter.incrementAndGet()
            }

        pipeline.sinkError(TestException())
        pipeline.sinkValue(5)
        Assertions.assertEquals(1, counter.get())
    }

    @Test
    fun testRunOn() {
        val latch = CountDownLatch(1)
        val specialValue = ThreadLocal<Int>()
        specialValue.set(0)

        val pipeline = Pipeline.create<Int>()
            .runOn {
                JobManagers.parallel()
            }.doOnValue {
                specialValue.set(it)
                latch.countDown()
            }

        pipeline.sinkValue(42)
        latch.await()
        Assertions.assertEquals(0, specialValue.get())
    }

    @Test
    fun testMap() {
        var tempResult: Try<Int> = Try.success(0)
        val successPipeline = Pipeline.create<Int>()
            .map {
                it * it
            }.doAlways {
                tempResult = it

            }
        successPipeline.sinkValue(5)
        Assertions.assertEquals(25, tempResult.get())

        val initialFailPipeline = Pipeline.create<Int>()
            .map {
                it * it
            }.doAlways {
                tempResult = it
            }
        initialFailPipeline.sinkError(TestException())
        Assertions.assertThrows(TestException::class.java, { tempResult.get() })

        val duringMapFailPipeline = Pipeline.create<Int>()
            .map {
                it * it
            }.doAlways {
                tempResult = it
            }
        duringMapFailPipeline.sinkError(TestException())
        Assertions.assertThrows(TestException::class.java, { tempResult.get() })
    }

    @Test
    fun testFlatMap() {
        var tempResult: Try<Int> = Try.success(0)

        val addOne = Pipeline.create<Int>()
            .map {
                it + 1
            }

        val successPipeline = Pipeline.create<Int>()
            .map {
                it * it
            }.flatMap {
                addOne
            }.doAlways {
                tempResult = it
            }

        successPipeline.sinkValue(5)
        Assertions.assertEquals(26, tempResult.get())

        successPipeline.sinkError(TestException())
        Assertions.assertThrows(TestException::class.java, { tempResult.get() })

        val errorPipeline = Pipeline.create<Int>()
            .map {
                it * it
            }.flatMap {
                Pipeline.create<Int>()
                    .map {
                        throw TestException()
                        @Suppress("UNREACHABLE_CODE")
                        it * it
                    }
            }.doAlways {
                tempResult = it
            }

        errorPipeline.sinkValue(5)
        Assertions.assertThrows(TestException::class.java, { tempResult.get() })
    }

    @Test
    fun testFlatMapTask() {
        var tempResult: Try<Int> = Try.success(0)

        val successTask = Task.just(42)

        val successPipeline = Pipeline.create<Int>()
            .flatMapTask(successTask)
            .doAlways {
                tempResult = it
            }

        successPipeline.sinkValue(12)
        Assertions.assertEquals(42, tempResult.get())

        val failTask = Task.fail<Int>(TestException())
        val failPipeline = Pipeline.create<Int>()
            .flatMapTask(failTask)
            .doAlways {
                tempResult = it
            }

        failPipeline.sinkValue(12)
        Assertions.assertThrows(TestException::class.java, { tempResult.get() })
    }

    @Test
    fun testFilter() {
        val counter = AtomicInteger(0)
        val pipeline = Pipeline.create<Int>()
            .filter {
                it > 0
            }.doOnValue {
                counter.incrementAndGet()
            }

        pipeline.sinkValue(0)
        pipeline.sinkValue(1)
        pipeline.sinkError(TestException())
        Assertions.assertEquals(1, counter.get())

    }
}