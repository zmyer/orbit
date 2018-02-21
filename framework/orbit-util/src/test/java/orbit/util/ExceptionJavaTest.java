/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.util;

import orbit.util.exception.ExceptionUtils;
import orbit.util.exception.OrbitException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExceptionJavaTest {
    @Test
    void testExceptionJavaAPI() {
        final OrbitException blankOrbitException = new OrbitException();
        Assertions.assertNull(blankOrbitException.getMessage());
        Assertions.assertNull(blankOrbitException.getCause());
        Assertions.assertTrue(ExceptionUtils.isCauseInChain(OrbitException.class, blankOrbitException));

        final OrbitException textOrbitException = new OrbitException("textOrbitException");
        Assertions.assertEquals("textOrbitException", textOrbitException.getMessage());
        Assertions.assertTrue(ExceptionUtils.isCauseInChain(OrbitException.class, textOrbitException));

        final OrbitException nullOrbitException = null;
        Assertions.assertFalse(ExceptionUtils.isCauseInChain(OrbitException.class, nullOrbitException));

        final RuntimeException nestedOrbitException = new RuntimeException("nested", new OrbitException());
        Assertions.assertTrue(ExceptionUtils.isCauseInChain(OrbitException.class, nestedOrbitException));
    }
}
