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

import io.matthewnelson.component.secure.random.internal.ifNotNullOrEmpty
import io.matthewnelson.component.secure.random.internal.commonNextBytesOf

/**
 * A cryptographically strong random number generator (RNG).
 *
 * @see [java.security.SecureRandom]
 * */
public actual class SecureRandom: java.security.SecureRandom {

    public actual constructor(): super()
    public constructor(seed: ByteArray): super(seed)

    /**
     * Returns a [ByteArray] of size [count], filled with
     * securely generated random data.
     *
     * @throws [IllegalArgumentException] if [count] is negative.
     * */
    @Throws(IllegalArgumentException::class)
    public actual fun nextBytesOf(count: Int): ByteArray = commonNextBytesOf(count)

    /**
     * Fills a [ByteArray] with securely generated random data.
     *
     * @see [java.security.SecureRandom.nextBytes]
     * */
    public actual override fun nextBytes(bytes: ByteArray?) { bytes.ifNotNullOrEmpty { super.nextBytes(this) } }
}
