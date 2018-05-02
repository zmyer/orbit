/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.runtime

import org.junit.jupiter.api.Test

class StageTest {
    @Test
    fun testStageStartBasic() {
        val stage = Stage.createStage(StageConfiguration())
        stage.start()
    }
}