/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent;

import orbit.concurrent.task.Task;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TaskJavaTest {

    @Test
    void testAsCompletableFuture() {
        try {
            final Task<Integer> successTask = Task.just(42);
            final CompletableFuture<Integer> successCf = successTask.asCompletableFuture();
            Assertions.assertEquals(42, successCf.get().intValue());

            final Task<Integer> failTask = Task.fail(new RuntimeException());
            final CompletableFuture<Integer> failCf = failTask.asCompletableFuture();
            Assertions.assertThrows(ExecutionException.class, failCf::get);

        } catch (Exception e) {

        }
    }

    @Test
    void testFromCompletableFuture() {
        final CompletableFuture<Integer> successCf = CompletableFuture.completedFuture(42);
        final Task<Integer> successTask = Task.fromCompletableFuture(successCf);
        Assertions.assertEquals(42, successTask.await().intValue());

        final CompletableFuture<Integer> failCf = new CompletableFuture<>();
        failCf.completeExceptionally(new RuntimeException());
        final Task<Integer> failTask = Task.fromCompletableFuture(failCf);
        Assertions.assertThrows(RuntimeException.class, failTask::await);
    }
}
