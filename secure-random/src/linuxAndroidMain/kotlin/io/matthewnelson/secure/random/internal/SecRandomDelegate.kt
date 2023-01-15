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
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert

internal actual abstract class SecRandomDelegate private actual constructor() {

    @Throws(SecRandomCopyException::class)
    internal actual abstract fun nextBytesCopyTo(size: Int, ptrBytes: CPointer<ByteVar>)

    internal actual companion object {

        actual fun instance(): SecRandomDelegate {
            return if (GetRandom.instance.isAvailable()) {
                SecRandomDelegateGetRandom
            } else {
                SecRandomDelegateURandom
            }
        }
    }

    private object SecRandomDelegateGetRandom: SecRandomDelegate() {

        @Throws(SecRandomCopyException::class)
        override fun nextBytesCopyTo(size: Int, ptrBytes: CPointer<ByteVar>) {
            @OptIn(UnsafeNumber::class)
            val result = GetRandom.instance.getrandom(ptrBytes, size.toULong().convert())
            if (result < 0) {
                throw SecRandomCopyException(errnoToString(result))
            }
        }
    }

    // TODO: Add fallback (Issue #25)
    private object SecRandomDelegateURandom: SecRandomDelegate() {

        @Throws(SecRandomCopyException::class)
        override fun nextBytesCopyTo(size: Int, ptrBytes: CPointer<ByteVar>) {
            throw SecRandomCopyException("SYS_getrandom not supported")
        }
    }
}