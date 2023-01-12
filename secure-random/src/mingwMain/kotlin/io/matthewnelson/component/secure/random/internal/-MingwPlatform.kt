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

import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.windows.BCRYPT_USE_SYSTEM_PREFERRED_RNG
import platform.windows.BCryptGenRandom
import platform.windows.PUCHAR

@OptIn(UnsafeNumber::class)
@Suppress("UNCHECKED_CAST")
internal actual fun ByteArray.nativeNextBytes() {
    val size = size.toULong()
    usePinned { pinned ->
        BCryptGenRandom(null, pinned.addressOf(0) as PUCHAR, size.convert(), BCRYPT_USE_SYSTEM_PREFERRED_RNG)
    }
}
