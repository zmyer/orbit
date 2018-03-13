/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.pipeline.impl

import orbit.concurrent.flow.Processor
import orbit.concurrent.flow.Subscriber
import orbit.concurrent.pipeline.Pipeline
import orbit.util.tries.Try
import java.util.concurrent.atomic.AtomicReference

internal abstract class AbstractPipelineImpl<S, T, R>: Processor<T, R>, Pipeline<S, R>() {
    private val listeners = AtomicReference(listOf<Subscriber<R>>())

    override fun subscribe(subscriber: Subscriber<R>) {
        do {
            val listenerList = listeners.get()
        } while(!listeners.compareAndSet(listenerList, listenerList + subscriber))
    }

    override fun onNext(item: Try<T>) {
        operator(item)
    }

    protected abstract fun operator(item: Try<T>)

    protected fun publish(item: Try<R>) {
        listeners.get().forEach {
            it.onNext(item)
        }
    }

    override fun onSink(item: Try<S>) {
        @Suppress("UNCHECKED_CAST")
        onNext(item as Try<T>)
    }
}