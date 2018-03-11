/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent

import orbit.concurrent.task.TaskContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.Semaphore

class TaskContextTest {
    @Test
    fun taskContextsAreAssignedIncreasingIds() {
        val id = Regex("\\d+").find(TaskContext().toString())!!.value.toInt()
        Assertions.assertEquals("TaskContext:${id + 1}", TaskContext().toString())
        Assertions.assertEquals("TaskContext:${id + 2}", TaskContext().toString())
        Assertions.assertEquals("TaskContext:${id + 3}", TaskContext().toString())
    }

    @Test
    fun taskContextHoldsProperties() {
        val taskContext = TaskContext()

        taskContext["key1"] = "value1"
        taskContext["key2"] = "value2"

        Assertions.assertEquals("value1", taskContext["key1"])
        Assertions.assertEquals("value2", taskContext["key2"])
    }

    @Test
    fun gettingUnsetPropertyReturnsNull() {
        val taskContext = TaskContext()

        Assertions.assertNull(taskContext["key"])
    }

    @Test
    fun currentTaskContextIsNullOnNewThread() {
        var taskContext: TaskContext? = null

        val thread = Thread {
            taskContext = TaskContext.current()
        }
        thread.start()
        thread.join()

        Assertions.assertNull(taskContext)
    }

    @Test
    fun poppingCurrentTaskContextSucceeds() {
        val context1 = TaskContext()
        val context2 = TaskContext()

        context1.push()
        context2.push()
        context1.push()
        context1.pop()
        context2.pop()
        context1.pop()
    }

    @Test
    fun poppingInterleavedTaskContextThrows() {
        val context1 = TaskContext()
        val context2 = TaskContext()

        context1.push()
        context2.push()

        Assertions.assertThrows(IllegalStateException::class.java) { context1.pop() }
    }

    @Test
    fun taskContextIsThreadLocal() {
        var thread1Context: TaskContext? = null
        var thread2Context: TaskContext? = null

        val context1 = TaskContext()
        val context2 = TaskContext()

        val mainThreadSem = Semaphore(0)
        val thread1Sem = Semaphore(0)
        val thread2Sem = Semaphore(0)

        val thread1 = Thread {
            context1.push()
            context2.push()
            thread1Context = TaskContext.current()
            mainThreadSem.release()
            thread1Sem.acquire()
            context2.pop()
            context1.pop()
            thread1Context = TaskContext.current()
            mainThreadSem.release()
            thread1Sem.acquire()
        }
        thread1.start()

        mainThreadSem.acquire()

        // thread1 is waiting on thread1Sem, check that current context there is context2
        Assertions.assertSame(context2, thread1Context)
        Assertions.assertSame(context2, TaskContext.currentFor(thread1))

        val thread2 = Thread {
            thread2Context = TaskContext.current()
            mainThreadSem.release()
            thread2Sem.acquire()
            context1.push()
            thread2Context = TaskContext.current()
            mainThreadSem.release()
            thread2Sem.acquire()
            context1.pop()
            thread2Context = TaskContext.current()
            mainThreadSem.release()
            thread2Sem.acquire()
        }
        thread2.start()

        mainThreadSem.acquire()

        // thread2 is waiting on thread2Sem, check that the context there was initially null
        Assertions.assertNull(thread2Context)
        Assertions.assertNull(TaskContext.currentFor(thread2))

        thread2Sem.release()
        mainThreadSem.acquire()

        // thread2 is waiting on thread2Sem again, check that the context there is context1
        Assertions.assertSame(context1, thread2Context)
        Assertions.assertSame(context1, TaskContext.currentFor(thread2))

        thread1Sem.release()
        thread2Sem.release()
        mainThreadSem.acquire()
        mainThreadSem.acquire()

        // Both threads have popped their contexts and are waiting on their semaphore,
        // check that their current contexts are both null
        Assertions.assertNull(thread1Context)
        Assertions.assertNull(thread2Context)
        Assertions.assertNull(TaskContext.currentFor(thread1))
        Assertions.assertNull(TaskContext.currentFor(thread2))

        thread1Sem.release()
        thread2Sem.release()

        thread1.join()
        thread2.join()
    }
}
