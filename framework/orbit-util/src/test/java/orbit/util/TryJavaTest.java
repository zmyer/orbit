/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util;

import orbit.util.tries.Try;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        final Try<String> fail = Try.create(() -> {
            throw new TryJavaTestException();
        });
        Assertions.assertTrue(fail.isFailure());
        Assertions.assertThrows(TryJavaTestException.class, fail::get);

        // Try an operator
        final Try<Integer> map = Try.create(() -> 5).map((v) -> v * v);
        Assertions.assertTrue(map.isSuccess());
        Assertions.assertEquals((Integer) 25, map.get());
    }

    @Test
    void testTryJavaOptionalConversions() {
        final Try<String> stringTry = Try.create(() -> "stringTry");
        final Optional<String> javaStringOptional = stringTry.asOptional();
        Assertions.assertTrue(javaStringOptional.isPresent());
        Assertions.assertEquals("stringTry", javaStringOptional.get());

        final Try<String> throwableTry = Try.create(() -> {
            throw new TryJavaTestException();
        });
        final Optional<String> javaEmptyOptional = throwableTry.asOptional();
        Assertions.assertFalse(javaEmptyOptional.isPresent());
    }
}