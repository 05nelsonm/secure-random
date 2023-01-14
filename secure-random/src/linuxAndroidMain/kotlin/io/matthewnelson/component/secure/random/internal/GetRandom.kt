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

package io.matthewnelson.component.secure.random.internal

import kotlinx.cinterop.*
import platform.posix.*
import kotlin.native.concurrent.AtomicInt

/**
 * Helper for:
 *  - Determining if system has [getrandom] available
 *  - Utilizing the [getrandom] syscall
 *
 * Must always check that [isAvailable] returns true before
 * calling [getrandom].
 *
 * https://man7.org/linux/man-pages/man2/getrandom.2.html
 * */
internal class GetRandom private constructor() {

    internal companion object {
        private const val NO_FLAGS: UInt = 0U
        // https://docs.piston.rs/dev_menu/libc/constant.SYS_getrandom.html
        private const val SYS_getrandom: Long = 318L
        // https://docs.piston.rs/dev_menu/libc/constant.GRND_NONBLOCK.html
        private const val GRND_NONBLOCK: UInt = 0x0001U

        private const val TRUE = 1
        private const val FALSE = 0
        private const val UNCHECKED = -1

        internal val instance = GetRandom()
    }

    private val hasGetRandom = AtomicInt(UNCHECKED)

    internal fun isAvailable(): Boolean {
        when (hasGetRandom.value) {
            FALSE -> return false
            TRUE -> return true
            else -> {
                val buf = ByteArray(1)
                val lock = buf.hashCode()

                try {
                    return checkGetRandom(buf, lock)
                } finally {
                    // If for some reason value wasn't updated to t/f
                    // release the lock by setting it back to UNCHECKED
                    hasGetRandom.compareAndSet(lock, UNCHECKED)
                }
            }
        }
    }

    private fun checkGetRandom(buf: ByteArray, lock: Int): Boolean {
        // acquire lock
        while (true) {
            when (hasGetRandom.value) {
                FALSE -> return false
                TRUE -> return true
                else -> {
                    if (hasGetRandom.compareAndSet(UNCHECKED, lock)) {
                        break
                    }
                }
            }
        }

        // Check non-blocking with 1 byte
        val result = buf.usePinned { pinned ->
            @OptIn(UnsafeNumber::class)
            getrandom(pinned.addressOf(0), buf.size.toULong().convert(), GRND_NONBLOCK)
        }

        // Set + return t/f
        if (result < 0) {
            @Suppress("LiftReturnOrAssignment")
            when (result) {
                ENOSYS, // No kernel support
                EPERM, // Blocked by seccomp
                -> {
                    hasGetRandom.compareAndSet(lock, FALSE)
                    return false
                }
                else -> {
                    hasGetRandom.compareAndSet(lock, TRUE)
                    return true
                }
            }
        } else {
            hasGetRandom.compareAndSet(lock, TRUE)
            return true
        }
    }

    /**
     * Must always call [isAvailable] beforehand.
     * */
    @OptIn(UnsafeNumber::class)
    internal fun getrandom(buf: CPointer<ByteVar>, buflen: size_t): Int = getrandom(buf, buflen, NO_FLAGS)

    @OptIn(UnsafeNumber::class)
    private fun getrandom(
        buf: CPointer<ByteVar>,
        buflen: size_t,
        flags: u_int,
    ): Int {
        @Suppress("RemoveRedundantCallsOfConversionMethods")
        return syscall(SYS_getrandom.convert(), buf, buflen, flags).toInt()
    }
}
