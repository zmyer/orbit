/*
 Copyright (C) 2015 - 2018 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <http://www.orbit.cloud>.
 See license in LICENSE.
 */

package orbit.concurrent.task

import java.util.Deque
import java.util.LinkedList
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * A thread-local context that flows across threads as tasks are scheduled and composed. Since multiple tasks might
 * be in flight at any time and being scheduled to different threads, each thread contains a stack of [TaskContext]
 * so that new instances don't overwrite other tasks' contexts.
 */
class TaskContext {
    companion object {
        private val contextStacks: ThreadLocal<Deque<TaskContext>> = ThreadLocal()
        private val contextStacksMap: WeakHashMap<Thread, Deque<TaskContext>> = WeakHashMap()
        private val nextId: AtomicLong = AtomicLong(1)

        /**
         * Gets the current [TaskContext].
         * @return The current [TaskContext] for the current thread.
         */
        @JvmStatic
        fun current(): TaskContext? = contextStacks.get()?.peekLast()

        /**
         * Gets the current [TaskContext] on a given thread.
         * @param thread A [Thread] reference.
         * @return The current [TaskContext] for the specified thread.
         */
        @JvmStatic
        @Synchronized
        fun currentFor(thread: Thread): TaskContext? = contextStacksMap[thread]?.peekLast()

        /**
         * Gets the set of threads that have [TaskContext]s.
         * @return The set of threads that have [TaskContext]s.
         */
        @JvmStatic
        @Synchronized
        fun activeThreads(): Set<Thread> = contextStacksMap.keys.toSet()
    }

    private val id = nextId.getAndIncrement()

    private val properties: ConcurrentHashMap<String, Any> = ConcurrentHashMap()

    /**
     * Push this [TaskContext] instance on the current thread's stack of [TaskContext]s.
     */
    fun push() {
        var stack = contextStacks.get()

        if (stack == null) {
            stack = LinkedList()
            contextStacks.set(stack)

            val currentThread = Thread.currentThread()

            synchronized(contextStacksMap, {
                contextStacksMap[currentThread] = stack
            })
        }

        stack.addLast(this)
    }

    /**
     * Pops this [TaskContext] instance from the current thread's stack of [TaskContext]s.
     * @throws IllegalStateException Thrown if this [TaskContext] instance is not at the top of the stack for
     * the current thread.
     */
    fun pop() {
        val stack = contextStacks.get()
                ?: throw IllegalStateException("Invalid execution context stack state: null trying to remove: $this")

        val taskContext = stack.pollLast()

        if (taskContext != this) {
            if (taskContext != null) {
                // Return it to the stack
                stack.addLast(taskContext)
            }

            throw IllegalStateException("Invalid execution context stack state: $stack trying to remove: $this")
        }
    }

    override fun toString(): String = "${javaClass.simpleName}:$id"

    operator fun get(name: String): Any? = properties[name]

    operator fun set(name: String, value: Any?) =
            if (value != null) {
                properties[name] = value
            } else {
                properties.remove(name)
            }
}