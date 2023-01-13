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
package io.matthewnelson.component.secure.random

import io.matthewnelson.component.secure.random.internal.commonNextBytesOf

/**
 * A cryptographically strong random number generator (RNG).
 *
 * @see [instanceStrong]
 * @see [java.security.SecureRandom]
 * */
public actual class SecureRandom {

    private val delegate: java.security.SecureRandom

    private constructor(delegate: java.security.SecureRandom) { this.delegate = delegate }
    public actual constructor(): this(java.security.SecureRandom())

    /**
     * Returns a [ByteArray] of size [count], filled with
     * securely generated random data.
     *
     * @throws [IllegalArgumentException] if [count] is negative.
     * @throws [SecRandomCopyException] if [nextBytesCopyTo] failed
     * */
    @Throws(IllegalArgumentException::class)
    public actual fun nextBytesOf(count: Int): ByteArray = commonNextBytesOf(count)

    /**
     * Fills a [ByteArray] with securely generated random data.
     * Does nothing if [bytes] is null or empty.
     *
     * @see [java.security.SecureRandom.nextBytes]
     * */
    public actual fun nextBytesCopyTo(bytes: ByteArray?) {
        bytes.ifNotNullOrEmpty {
            delegate.nextBytes(this)
        }
    }

    public actual companion object {

        /**
         * Returns a strong instance suitable for private key generation.
         *
         * @see [java.security.SecureRandom.getInstanceStrong]
         * @throws [NoSuchAlgorithmException] if no algorithm is available
         * */
        @JvmStatic
        @Throws(NoSuchAlgorithmException::class)
        public actual fun instanceStrong(): SecureRandom {
            return SecureRandom(java.security.SecureRandom.getInstanceStrong())
        }
    }
}
