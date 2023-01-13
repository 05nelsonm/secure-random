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
 *
 **/
package io.matthewnelson.component.secure.random.internal

import io.matthewnelson.component.secure.random.NoSuchAlgorithmException
import io.matthewnelson.component.secure.random.SecRandomCopyException
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.convert
import platform.windows.*

/**
 * https://learn.microsoft.com/en-us/windows/win32/api/bcrypt/nf-bcrypt-bcryptgenrandom
 * */
internal actual abstract class SecRandomDelegate private actual constructor() {

    @Throws(SecRandomCopyException::class)
    internal actual abstract fun nextBytesCopyTo(size: Int, ptrBytes: CPointer<ByteVar>)

    // Default instance
    internal actual companion object: SecRandomDelegate() {

        @Suppress("UNCHECKED_CAST")
        @Throws(SecRandomCopyException::class)
        actual override fun nextBytesCopyTo(size: Int, ptrBytes: CPointer<ByteVar>) {
            // TODO: Add fallbacks for pre Vista SP2 (Issue #8)
            val status = BCryptGenRandom(
                null,
                ptrBytes as PUCHAR,
                size.toULong().convert(),
                BCRYPT_USE_SYSTEM_PREFERRED_RNG,
            ).toUInt()

            when (status) {
                STATUS_INVALID_HANDLE,
                STATUS_INVALID_PARAMETER -> throw SecRandomCopyException(errorToString(status))
            }
        }
    }

    internal actual class Strong private actual constructor(): SecRandomDelegate() {

        @Throws(SecRandomCopyException::class)
        override fun nextBytesCopyTo(size: Int, ptrBytes: CPointer<ByteVar>) {
            throw SecRandomCopyException("Not yet implemented")
        }

        internal actual companion object {
            @Throws(NoSuchAlgorithmException::class)
            internal actual fun instance(): Strong {
                throw NoSuchAlgorithmException("Not yet implemented")
            }
        }
    }

}
