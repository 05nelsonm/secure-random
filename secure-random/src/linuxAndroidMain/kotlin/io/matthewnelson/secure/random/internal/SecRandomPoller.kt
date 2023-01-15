/*
 * Copyright (c) 2023 Matthew Nelson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package io.matthewnelson.secure.random.internal

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlin.native.concurrent.AtomicInt

internal abstract class SecRandomPoller {

    private companion object {
        private const val TRUE = 1
        private const val FALSE = 0
        private const val UNKNOWN = -1
    }

    private val result = AtomicInt(UNKNOWN)

    protected fun pollingResult(poll: (buf: CPointer<ByteVar>, size: Int) -> Boolean): Boolean {
        when (result.value) {
            TRUE -> return true
            FALSE -> return false
            else -> {
                val buf = ByteArray(1)
                val lock = buf.hashCode()

                try {
                    return startPolling(buf, lock, poll)
                } finally {
                    // If for some reason value wasn't updated to t/f,
                    // ensure the lock is reset back to UNKNOWN so that
                    // it can be obtained again.
                    result.compareAndSet(lock, UNKNOWN)
                }
            }
        }
    }

    private fun startPolling(
        buf: ByteArray,
        lock: Int,
        poll: (buf: CPointer<ByteVar>, size: Int) -> Boolean
    ): Boolean {
        while (true) {
            when (result.value) {
                TRUE -> return true
                FALSE -> return false
                else -> {
                    if (result.compareAndSet(UNKNOWN, lock)) {
                        break
                    }
                }
            }
        }

        val result: Boolean = buf.usePinned { pinned ->
            poll.invoke(pinned.addressOf(0), buf.size)
        }

        this.result.compareAndSet(lock, if (result) TRUE else FALSE)

        return result
    }
}
