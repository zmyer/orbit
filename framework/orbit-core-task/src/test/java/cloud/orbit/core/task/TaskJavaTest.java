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

package cloud.orbit.core.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

class TaskJavaTest {
    @Test
    void toCompletableFutureTest() {
        try {
            final Task<Integer> successTask = Task.just(42);
            final CompletableFuture<Integer> successCf = successTask.toCompletableFuture();
            Assertions.assertEquals(42, successCf.get().intValue());

            final Task<Integer> failTask = Task.fail(new RuntimeException());
            final CompletableFuture<Integer> failCf = failTask.toCompletableFuture();
            Assertions.assertThrows(ExecutionException.class, failCf::get);

        } catch(Exception e) {

        }
    }

    @Test
    void fromCompletableFutureTest() {
        final CompletableFuture<Integer> successCf = CompletableFuture.completedFuture(42);
        final Task<Integer> successTask = Task.fromCompletableFuture(successCf);
        Assertions.assertEquals(42, successTask.await().intValue());

        final CompletableFuture<Integer> failCf = new CompletableFuture<>();
        failCf.completeExceptionally(new RuntimeException());
        final Task<Integer> failTask = Task.fromCompletableFuture(failCf);
        Assertions.assertThrows(RuntimeException.class, failTask::await);
    }
}
