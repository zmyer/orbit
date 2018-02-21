/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

module orbit.concurrent {
    exports orbit.concurrent.job;
    exports orbit.concurrent.pipeline;
    exports orbit.concurrent.task;

    requires orbit.logging;
    requires orbit.util;

    requires kotlin.stdlib;
}