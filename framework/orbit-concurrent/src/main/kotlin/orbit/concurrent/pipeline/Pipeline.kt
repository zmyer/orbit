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

package orbit.concurrent.pipeline

import orbit.concurrent.pipeline.operator.PipelineHandleOperator
import orbit.concurrent.pipeline.operator.PipelineMapOperator
import orbit.concurrent.pipeline.operator.PipelineOnErrorOperator
import orbit.concurrent.pipeline.operator.PipelineOnValueOperator
import orbit.concurrent.pipeline.operator.PipelineOperator
import orbit.util.tries.Try
import java.util.concurrent.atomic.AtomicReference

abstract class Pipeline<T> {
    private val listeners = AtomicReference(listOf<PipelineOperator<T, *>>())

    private fun addListener(pipelineOperator: PipelineOperator<T, *>) {
        while(true) {
            val listenerList = listeners.get()
            val newList = listenerList + pipelineOperator
            if (listeners.compareAndSet(listenerList, newList)) {
                break
            }
        }
    }

    internal fun triggerListeners(value: Try<T>) {
        listeners.get().forEach {
            it.onNext(value)
        }
    }

    infix fun handle(body: (Try<T>) -> Unit): Pipeline<T> =
            PipelineHandleOperator(body).apply { addListener(this) }

    infix fun onValue(body: (T) -> Unit): Pipeline<T> =
            PipelineOnValueOperator(body).apply { addListener(this) }

    infix fun onError(body: (Throwable) -> Unit): Pipeline<T> =
            PipelineOnErrorOperator<T>(body).apply { addListener(this) }

    infix fun <V> map(body: (T) -> V): Pipeline<V> =
            PipelineMapOperator(body).apply { addListener(this) }
}