/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent

import orbit.concurrent.job.JobManagers
import orbit.concurrent.task.Task
import orbit.concurrent.task.TaskContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.function.Supplier

class TaskContextOperatorsTest {
    @Test
    fun currentTaskContextFlowsToTaskCreatedWithApplyOperator() {
        var capturedTaskContext: TaskContext? = null

        val taskContext = TaskContext()
        taskContext.push()

        // Use a semaphore to keep the task uncompleted until after the context is popped from the current thread
        val sem = Semaphore(0)

        val task = Task(JobManagers.newSingleThread()) { sem.acquire() }

        taskContext.pop()
        sem.release()
        task.await()

        runOnNewThread {
            task.doAlways { capturedTaskContext = TaskContext.current() }.await()
        }

        Assertions.assertSame(taskContext, capturedTaskContext)
    }

    @Test
    fun currentTaskContextFlowsToTaskCreatedWithFromCompletableFutureOperator() {
        var capturedTaskContext: TaskContext? = null

        val taskContext = TaskContext()
        val otherTaskContext = TaskContext()
        taskContext.push()

        val sem = Semaphore(0)
        val executor = Executors.newSingleThreadExecutor()

        val completableFuture = CompletableFuture.supplyAsync(Supplier {
            otherTaskContext.push()
            sem.acquire()
        }, executor)

        val task = Task.fromCompletableFuture(completableFuture)

        taskContext.pop()
        sem.release()
        task.await()

        runOnNewThread {
            task.doAlways { capturedTaskContext = TaskContext.current() }
        }

        executor.submit({ otherTaskContext.pop() })

        Assertions.assertSame(taskContext, capturedTaskContext)
    }

    @Test
    fun currentTaskContextFlowsToTaskCreatedWithAllOfOperator() {
        var capturedTaskContext: TaskContext? = null

        val sem = Semaphore(0)

        val task1TaskContext = TaskContext()
        task1TaskContext.push()
        val task1 = Task(JobManagers.newSingleThread()) { sem.acquire() }

        val task2TaskContext = TaskContext()
        task2TaskContext.push()
        val task2 = Task(JobManagers.newSingleThread()) { sem.acquire() }

        val taskContext = TaskContext()
        taskContext.push()
        val task = Task.allOf(task1, task2)

        taskContext.pop()
        task2TaskContext.pop()
        task1TaskContext.pop()

        sem.release()
        sem.release()
        task.await()

        runOnNewThread {
            task.doAlways { capturedTaskContext = TaskContext.current() }.await()
        }

        Assertions.assertSame(taskContext, capturedTaskContext)
    }

    @Test
    fun currentTaskContextFlowsToTaskCreatedWithAnyOfOperator() {
        var capturedTaskContext: TaskContext? = null

        val sem = Semaphore(0)

        val task1TaskContext = TaskContext()
        task1TaskContext.push()
        val task1 = Task(JobManagers.newSingleThread()) { sem.acquire() }

        val task2TaskContext = TaskContext()
        task2TaskContext.push()
        val task2 = Task(JobManagers.newSingleThread()) { sem.acquire() }

        val taskContext = TaskContext()
        taskContext.push()
        val task = Task.anyOf(task1, task2)

        taskContext.pop()
        task2TaskContext.pop()
        task1TaskContext.pop()

        sem.release()
        task.await()

        runOnNewThread {
            task.doAlways { capturedTaskContext = TaskContext.current() }.await()
        }

        // Let the remaining task finish
        sem.release()

        Assertions.assertSame(taskContext, capturedTaskContext)
    }

    @Test
    fun taskContextFlowsWithDoAlwaysOperatorOnSuccess() {
        var capturedTaskContext: TaskContext? = null

        val taskContext = TaskContext()
        taskContext.push()

        val sem = Semaphore(0)

        val task = Task(JobManagers.newSingleThread()) { sem.acquire() }

        taskContext.pop()
        sem.release()
        task.await()

        runOnNewThread {
            task.doAlways { capturedTaskContext = TaskContext.current() }.await()
        }

        Assertions.assertSame(taskContext, capturedTaskContext)
    }

    @Test
    fun taskContextFlowsWithDoAlwaysOperatorOnFailure() {
        var capturedTaskContext: TaskContext? = null

        val taskContext = TaskContext()
        taskContext.push()

        val sem = Semaphore(0)

        val task = Task(JobManagers.newSingleThread()) {
            sem.acquire()
            throw RuntimeException()
        }

        taskContext.pop()
        sem.release()

        try {
            task.await()
        } catch (e: Exception) {
        }

        runOnNewThread {
            task.doAlways { capturedTaskContext = TaskContext.current() }.await()
        }

        Assertions.assertSame(taskContext, capturedTaskContext)
    }

    @Test
    fun taskContextFlowsWithDoOnErrorOperator() {
        var capturedTaskContext: TaskContext? = null

        val taskContext = TaskContext()
        taskContext.push()

        val sem = Semaphore(0)

        val task = Task(JobManagers.newSingleThread()) {
            sem.acquire()
            throw RuntimeException()
        }

        taskContext.pop()
        sem.release()

        try {
            task.await()
        } catch (e: Exception) {
        }

        runOnNewThread {
            task.doOnError { capturedTaskContext = TaskContext.current() }.await()
        }

        Assertions.assertSame(taskContext, capturedTaskContext)
    }

    @Test
    fun taskContextFlowsWithDoOnValueOperator() {
        var capturedTaskContext: TaskContext? = null

        val taskContext = TaskContext()
        taskContext.push()

        val sem = Semaphore(0)

        val task = Task(JobManagers.newSingleThread()) { sem.acquire() }

        taskContext.pop()
        sem.release()
        task.await()

        runOnNewThread {
            task.doOnValue { capturedTaskContext = TaskContext.current() }.await()
        }

        Assertions.assertEquals(taskContext, capturedTaskContext)
    }

    @Test
    fun taskContextFlowsWithFlatMapOperator() {
        var capuredTaskContext: TaskContext? = null

        val taskContext = TaskContext()
        taskContext.push()

        val sem = Semaphore(0)

        val task = Task(JobManagers.newSingleThread()) { sem.acquire() }

        taskContext.pop()
        sem.release()
        task.await()

        runOnNewThread {
            task.flatMap { Task { capuredTaskContext = TaskContext.current() } }.await()
        }

        Assertions.assertEquals(taskContext, capuredTaskContext)
    }

    @Test
    fun taskContextFlowsWithImmediateValueOperator() {
        var capturedTaskContext: TaskContext? = null

        val taskContext = TaskContext()
        taskContext.push()

        val task = Task.just(Unit)

        taskContext.pop()
        task.await()

        runOnNewThread {
            task.doAlways { capturedTaskContext = TaskContext.current() }.await()
        }

        Assertions.assertEquals(taskContext, capturedTaskContext)
    }

    @Test
    fun taskContextFlowsWithMapOperator() {
        var capturedTaskContext: TaskContext? = null

        val taskContext = TaskContext()
        taskContext.push()

        val sem = Semaphore(0)

        val task = Task(JobManagers.newSingleThread()) { sem.acquire() }

        taskContext.pop()
        sem.release()
        task.await()

        runOnNewThread {
            task.map { capturedTaskContext = TaskContext.current() }.await()
        }

        Assertions.assertEquals(taskContext, capturedTaskContext)
    }

    @Test
    fun taskContextFlowsWithRunOnOperator() {
        var capturedTaskContext: TaskContext? = null

        val taskContext = TaskContext()
        taskContext.push()

        val sem = Semaphore(0)

        val task = Task { sem.acquire() }.runOn(JobManagers.newSingleThread())

        taskContext.pop()
        sem.release()
        task.await()

        runOnNewThread {
            task.doAlways { capturedTaskContext = TaskContext.current() }.await()
        }

        Assertions.assertEquals(taskContext, capturedTaskContext)
    }

    private fun runOnNewThread(body: () -> Unit) {
        val thread = Thread { body() }
        thread.start()
        thread.join()
    }
}