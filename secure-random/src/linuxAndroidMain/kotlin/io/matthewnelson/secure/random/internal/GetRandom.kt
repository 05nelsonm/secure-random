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
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import platform.posix.*

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
internal class GetRandom private constructor(): SecRandomPoller() {

    internal companion object {
        private const val NO_FLAGS: UInt = 0U
        // https://docs.piston.rs/dev_menu/libc/constant.SYS_getrandom.html
        private const val SYS_getrandom: Long = 318L
        // https://docs.piston.rs/dev_menu/libc/constant.GRND_NONBLOCK.html
        private const val GRND_NONBLOCK: UInt = 0x0001U

        internal val instance = GetRandom()
    }

    internal fun isAvailable(): Boolean {
        return pollingResult { buf, size ->
            @OptIn(UnsafeNumber::class)
            val result = getrandom(buf, size.toULong().convert(), GRND_NONBLOCK)
            if (result < 0) {
                when (result) {
                    ENOSYS, // No kernel support
                    EPERM, // Blocked by seccomp
                    -> false
                    else
                    -> true
                }
            } else {
                true
            }
        }
    }

    /**
     * Must always call [isAvailable] beforehand.
     * */
    @OptIn(UnsafeNumber::class)
    @Throws(SecRandomCopyException::class)
    internal fun getrandom(buf: CPointer<ByteVar>, buflen: Int) {
        val result = getrandom(buf, buflen.toULong().convert(), NO_FLAGS)
        if (result < 0) throw SecRandomCopyException(errnoToString(result))
    }

    @OptIn(UnsafeNumber::class)
    private fun getrandom(
        buf: CPointer<ByteVar>,
        buflen: size_t,
        flags: u_int,
    ): Int {
        return syscall(SYS_getrandom.convert(), buf, buflen, flags).convert()
    }
}
