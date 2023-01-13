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

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class SecureRandomUnitTest {

    @Test
    fun givenNextBytesOf_whenCountNegative_thenThrows() {
        try {
            SecureRandom().nextBytesOf(-1)
            fail()
        } catch (_: IllegalArgumentException) {
            // pass
        }
    }

    @Test
    fun givenNextBytesOf_whenCount0_thenReturnsEmpty() {
        assertTrue(SecureRandom().nextBytesOf(0).isEmpty())
    }

    @Test
    fun givenByteArray_whenNextBytes_thenIsFilledWithData() {
        val bytes = ByteArray(100)
        val emptyByte = bytes[0]
        SecureRandom().nextBytesCopyTo(bytes)

        var emptyCount = 0
        bytes.forEach {
            if (it == emptyByte) {
                emptyCount++
            }
        }
        println(bytes.toList())

        // Some bytes will remain empty so cannot check if all indexes
        // were filled. We're just trying to verify that something happened,
        // so ensuring that 90% of the array was filled is good enough imo.
        assertTrue(emptyCount < 10)
    }
}
