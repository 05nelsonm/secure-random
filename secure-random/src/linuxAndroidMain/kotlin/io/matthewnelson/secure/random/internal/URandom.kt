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
@file:Suppress("SpellCheckingInspection", "UnnecessaryOptInAnnotation")

package io.matthewnelson.secure.random.internal

import io.matthewnelson.secure.random.SecRandomCopyException
import kotlinx.cinterop.*
import platform.posix.*
import kotlin.native.concurrent.AtomicInt

internal class URandom private constructor(): SecRandomPoller() {

    internal companion object {
        private const val INFINITE_TIMEOUT = -1

        internal val instance = URandom()
    }

    private val lock = Lock()

    @Throws(SecRandomCopyException::class)
    internal fun readBytesTo(buf: CPointer<ByteVar>, buflen: Int) {
        ensureSeeded()
        lock.withLock {
            @OptIn(UnsafeNumber::class)
            withReadOnlyFD("/dev/urandom") { fd ->
                val result = read(fd, buf, buflen.toULong().convert())
                if (result < 0) throw SecRandomCopyException(errnoToString(errno))
            }
        }
    }

    /**
     * Poll /dev/random once and only once to ensure
     * /dev/urandom is seeded and ready to use.
     * */
    private fun ensureSeeded() {
        pollingResult { _, _ ->
            withReadOnlyFD("/dev/random") { fd ->
                memScoped {
                    val pollFd = nativeHeap.alloc<pollfd>()
                    pollFd.apply {
                        this.fd = fd
                        events = POLLIN.convert()
                        revents = 0
                    }

                    while (true) {
                        @OptIn(UnsafeNumber::class)
                        val result = poll(pollFd.ptr, 1, INFINITE_TIMEOUT)
                        if (result >= 0) {
                            break
                        }
                        when (val err = errno) {
                            EINTR,
                            EAGAIN -> continue
                            else -> throw SecRandomCopyException(errnoToString(err))
                        }
                    }
                }
            }

            true
        }
    }

    private fun withReadOnlyFD(path: String, block: (fd: Int) -> Unit) {
        val fd = open(path, O_RDONLY, null)
        if (fd == -1) throw SecRandomCopyException(errnoToString(errno))

        try {
            block.invoke(fd)
        } finally {
            close(fd)
        }
    }

    private inner class Lock {

        // 0: Unlocked
        // *: Locked
        private val lock = AtomicInt(0)

        fun <T> withLock(block: () -> T): T {
            val lockId = Any().hashCode()

            try {
                while (true) {
                    if (lock.compareAndSet(0, lockId)) {
                        break
                    }
                }

                return block.invoke()
            } finally {
                lock.compareAndSet(lockId, 0)
            }
        }
    }
}
