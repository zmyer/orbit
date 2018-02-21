/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task.operator

import orbit.concurrent.task.Task
import orbit.util.tries.Try

internal abstract class TaskOperator<I, O>: Task<O>() {
     internal abstract fun onFulfilled(result: Try<I>)
}