/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent;

import orbit.concurrent.task.TaskContext;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskContextJavaTest {

    @Test
    public void testCurrent() {
        TaskContext taskContext = new TaskContext();
        taskContext.push();

        Assertions.assertSame(taskContext, TaskContext.current());

        taskContext.pop();
    }

    @Test
    public void testCurrentFor() throws InterruptedException {
        TaskContext taskContext = new TaskContext();
        Semaphore mainThreadSemaphore = new Semaphore(0);
        Semaphore threadSemaphore = new Semaphore(0);

        Thread thread = new Thread(() -> {
            try {
                taskContext.push();
                mainThreadSemaphore.release();
                threadSemaphore.acquire();
                taskContext.pop();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        mainThreadSemaphore.acquire();

        Assertions.assertSame(taskContext, TaskContext.currentFor(thread));

        threadSemaphore.release();
        thread.join();
    }

    @Test
    public void testActiveThreads() throws InterruptedException {
        TaskContext taskContext = new TaskContext();
        taskContext.push();

        Semaphore mainThreadSemaphore = new Semaphore(0);
        Semaphore threadSemaphore = new Semaphore(0);

        Thread thread = new Thread(() -> {
            try {
                TaskContext otherTaskContext = new TaskContext();
                otherTaskContext.push();
                mainThreadSemaphore.release();
                threadSemaphore.acquire();
                otherTaskContext.pop();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        thread.start();
        mainThreadSemaphore.acquire();

        Set<Thread> threadsWithTaskContexts = TaskContext.activeThreads();
        // Can't assertEquals because threads from other tests might be on the active thread set
        Assertions.assertTrue(threadsWithTaskContexts.size() >= 2);
        Assertions.assertTrue(threadsWithTaskContexts.contains(Thread.currentThread()));
        Assertions.assertTrue(threadsWithTaskContexts.contains(thread));

        threadSemaphore.release();
        thread.join();
    }

    @Test
    public void testProperties() {
        TaskContext taskContext = new TaskContext();

        taskContext.set("key", "value");

        Assertions.assertEquals("value", taskContext.get("key"));
    }
}
