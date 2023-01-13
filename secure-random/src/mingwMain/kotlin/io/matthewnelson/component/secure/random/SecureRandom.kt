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
package io.matthewnelson.component.secure.random

import io.matthewnelson.component.secure.random.internal.commonNextBytesOf
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.windows.BCRYPT_USE_SYSTEM_PREFERRED_RNG
import platform.windows.BCryptGenRandom
import platform.windows.PUCHAR

/**
 * A cryptographically strong random number generator (RNG).
 * */
public actual class SecureRandom {

    // TODO: Add provider constructor for strong instance
    @Suppress("ConvertSecondaryConstructorToPrimary")
    public actual constructor()

    /**
     * Returns a [ByteArray] of size [count], filled with
     * securely generated random data.
     *
     * @throws [IllegalArgumentException] if [count] is negative.
     * @throws [SecRandomCopyException] if [nextBytesCopyTo] failed
     * */
    @Throws(IllegalArgumentException::class, SecRandomCopyException::class)
    public actual fun nextBytesOf(count: Int): ByteArray = commonNextBytesOf(count)

    /**
     * Fills a [ByteArray] with securely generated random data.
     * Does nothing if [bytes] is null or empty.
     *
     * https://learn.microsoft.com/en-us/windows/win32/api/bcrypt/nf-bcrypt-bcryptgenrandom
     *
     * @throws [SecRandomCopyException] if procurement of securely random data failed
     * */
    @OptIn(UnsafeNumber::class)
    @Suppress("UNCHECKED_CAST")
    @Throws(SecRandomCopyException::class)
    public actual fun nextBytesCopyTo(bytes: ByteArray?) {
        bytes.ifNotNullOrEmpty {
            val size = size.toULong()

            // TODO: Move to provider
            // TODO: Throw on failure
            usePinned { pinned ->
                BCryptGenRandom(null, pinned.addressOf(0) as PUCHAR, size.convert(), BCRYPT_USE_SYSTEM_PREFERRED_RNG)
            }
        }
    }

    public actual companion object {

        /**
         * Returns a strong instance suitable for using with private key generation
         *
         * @throws [NoSuchAlgorithmException] if no algorithm is available
         * */
        @Throws(NoSuchAlgorithmException::class)
        public actual fun instanceStrong(): SecureRandom {
            throw NoSuchAlgorithmException("Not yet implemented")
        }
    }
}
