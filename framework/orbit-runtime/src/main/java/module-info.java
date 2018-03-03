/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

module orbit.runtime {
    exports orbit.runtime;

    requires orbit.logging;
    requires orbit.util;
    requires orbit.concurrent;
    requires orbit.net;

    requires kotlin.stdlib;
}