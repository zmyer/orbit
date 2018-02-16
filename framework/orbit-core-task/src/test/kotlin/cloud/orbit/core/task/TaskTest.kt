/*
 Copyright (C) 2018 Electronic Arts Inc.  All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1.  Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2.  Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
     its contributors may be used to endorse or promote products derived
     from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package cloud.orbit.core.task

import cloud.orbit.core.concurrent.JobManagers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TaskTest {
    private class TaskTestException: RuntimeException() { }

    @Test
    fun basicTest() {
        val success = Task { "success" }
        Assertions.assertEquals("success", success.await())

        val fail = Task { throw TaskTestException() } map {}
        Assertions.assertThrows(TaskTestException::class.java, { fail.await() })
    }

    @Test
    fun mapTest() {
        val success = Task { 5 } map { it * it }
        Assertions.assertEquals(25, success.await())

        val initialFail = Task<Int> { throw TaskTestException() } map { it * it }
        Assertions.assertThrows(TaskTestException::class.java, { initialFail.await() })

        val mapFail = Task { 5 } map { throw TaskTestException() }
        Assertions.assertThrows(TaskTestException::class.java, { mapFail.await() })
    }

    @Test
    fun flatMapTest() {
        val success = Task { 5 } flatMap { x-> Task { x * x }}
        Assertions.assertEquals(25, success.await())

        val initialFail = Task<Int> { throw TaskTestException() } flatMap { x-> Task { x * x }}
        Assertions.assertThrows(TaskTestException::class.java, { initialFail.await() })

        @Suppress("UNREACHABLE_CODE")
        val flatMapFail = Task { 5 } flatMap { throw TaskTestException(); Task { 5 } }
        Assertions.assertThrows(TaskTestException::class.java, { flatMapFail.await() })

        @Suppress("UNREACHABLE_CODE")
        val flatMapNestedFail = Task { 5 } flatMap { Task { throw TaskTestException(); 5 } }
        Assertions.assertThrows(TaskTestException::class.java, { flatMapNestedFail.await() })
    }

    @Test
    fun handleTest() {
        var didFire: Boolean

        didFire = false
        val success = Task { "success" } handle { it onSuccess { didFire = true }}
        Assertions.assertEquals("success", success.await())
        Assertions.assertTrue(didFire)

        didFire = false
        val fail = Task { throw TaskTestException() } handle { it onFailure { didFire = true }}
        Assertions.assertThrows(TaskTestException::class.java, { fail.await() })
        Assertions.assertTrue(didFire)
    }

    @Test
    fun onSuccessTest() {
        var didFire: Boolean

        didFire = false
        val success = Task { "success" } onSuccess { didFire = true }
        Assertions.assertEquals("success", success.await())
        Assertions.assertTrue(didFire)

        didFire = false
        val fail = Task { throw TaskTestException() } onSuccess { didFire = true }
        Assertions.assertThrows(TaskTestException::class.java, { fail.await() })
        Assertions.assertFalse(didFire)
    }

    @Test
    fun onFailureTest() {
        var didFire: Boolean

        didFire = false
        val success = Task { "success" }
        success onFailure  { didFire = true }
        Assertions.assertEquals("success", success.await())
        Assertions.assertFalse(didFire)

        didFire = false
        val fail = Task { throw TaskTestException() } onFailure { didFire = true }
        Assertions.assertThrows(TaskTestException::class.java, { fail.await() })
        Assertions.assertTrue(didFire)
    }

    @Test
    fun forceJobManagerTest() {
        val newThread = JobManagers.newSingleThread()
        val dummyThread = JobManagers.newSingleThread()
        val threadLocal = ThreadLocal<Int>()

        var shouldMatch = false
        var shouldNotMatch = true

        val task = Task(newThread) {
            threadLocal.set(42)
        } forceJobManager {
            newThread
        } onSuccess {
            shouldMatch = (threadLocal.get() == 42)
        } forceJobManager {
            dummyThread
        } onSuccess  {
            shouldNotMatch = (threadLocal.get() == 42)
        }
        task.await()
        Assertions.assertTrue(shouldMatch)
        Assertions.assertFalse(shouldNotMatch)
    }

    @Test
    fun delayedTest() {
        var didFire: Boolean

        didFire = false
        val success = Task { "success" }
        success.await()
        success onSuccess { didFire = true }
        Assertions.assertTrue(didFire)

    }

    @Test
    fun justTest() {
        var didTrigger = false
        val just = Task.just(42) onSuccess { didTrigger = true }
        Assertions.assertEquals(42, just.await())
        Assertions.assertTrue(didTrigger)
    }

    @Test
    fun emptyTest() {
        var didTrigger = false
        val empty = Task.empty() onSuccess { didTrigger = true }
        empty.await()
        Assertions.assertTrue(didTrigger)
    }

    @Test
    fun failTest() {
        var didTrigger = false
        val empty = Task.fail<Int>(TaskTestException()) onFailure { didTrigger = true }
        Assertions.assertThrows(TaskTestException::class.java, { empty.await() })
        Assertions.assertTrue(didTrigger)
    }


}