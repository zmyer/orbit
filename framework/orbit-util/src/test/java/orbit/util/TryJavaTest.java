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

package orbit.util;

import orbit.util.tries.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class TryJavaTest {
    private static class TryJavaTestException extends RuntimeException {

    }

    @Test
    void testTryJavaAPI() {
        // Basic success
        final Try<String> success = Try.create(() -> "success");
        Assertions.assertTrue(success.isSuccess());
        Assertions.assertEquals("success", success.get());

        // Basic fail
        final Try<String> fail = Try.create(() -> { throw new TryJavaTestException(); });
        Assertions.assertTrue(fail.isFailure());
        Assertions.assertThrows(TryJavaTestException.class, fail::get);

        // Try an operator
        final Try<Integer> map = Try.create(() -> 5).map((v) -> v*v);
        Assertions.assertTrue(map.isSuccess());
        Assertions.assertEquals((Integer) 25, map.get());
    }

    @Test
    void testTryJavaOptionalConversions() {
        final Try<String> stringTry = Try.create(() -> "stringTry");
        final Optional<String> javaStringOptional = stringTry.asOptional();
        Assertions.assertTrue(javaStringOptional.isPresent());
        Assertions.assertEquals("stringTry", javaStringOptional.get());

        final Try<String> throwableTry = Try.create(() -> { throw new TryJavaTestException(); });
        final Optional<String> javaEmptyOptional = throwableTry.asOptional();
        Assertions.assertFalse(javaEmptyOptional.isPresent());
    }
}