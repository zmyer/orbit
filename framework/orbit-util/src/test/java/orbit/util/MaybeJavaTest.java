/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util;

import orbit.util.maybe.Maybe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MaybeJavaTest {

    @Test
    @SuppressWarnings("unchecked")
    void testMaybeJavaAPI() {
        Maybe<String> some = Maybe.just("some");
        Assertions.assertTrue(some.isPresent());
        Assertions.assertEquals("some", some.get());

        Maybe<String> none = Maybe.empty();
        Assertions.assertTrue(none.isEmpty());
        Assertions.assertThrows(Throwable.class, none::get);
    }
}
