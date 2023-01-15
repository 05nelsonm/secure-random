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

import io.matthewnelson.secure.random.SecRandomCopyException
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.reinterpret
import platform.windows.BCRYPT_USE_SYSTEM_PREFERRED_RNG
import platform.windows.BCryptGenRandom
import platform.windows.STATUS_INVALID_HANDLE
import platform.windows.STATUS_INVALID_PARAMETER

/**
 * https://learn.microsoft.com/en-us/windows/win32/api/bcrypt/nf-bcrypt-bcryptgenrandom
 * */
internal actual abstract class SecRandomDelegate private actual constructor() {

    @Throws(SecRandomCopyException::class)
    internal actual abstract fun nextBytesCopyTo(bytes: Pinned<ByteArray>, size: Int)

    internal actual companion object {

        internal actual fun instance(): SecRandomDelegate {
            // TODO: Add fallbacks for pre Vista SP2 (Issue #8)
            return SecRandomDelegateMingwVistaSP2
        }
    }

    private object SecRandomDelegateMingwVistaSP2: SecRandomDelegate() {

        @Throws(SecRandomCopyException::class)
        override fun nextBytesCopyTo(bytes: Pinned<ByteArray>, size: Int) {
            val status = BCryptGenRandom(
                null,
                bytes.addressOf(0).reinterpret(),
                size.toULong().convert(),
                BCRYPT_USE_SYSTEM_PREFERRED_RNG,
            ).toUInt()

            when (status) {
                STATUS_INVALID_HANDLE,
                STATUS_INVALID_PARAMETER -> throw SecRandomCopyException(errorToString(status))
            }
        }
    }
}
