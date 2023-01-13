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

internal expect abstract class SecRandomDelegate private constructor() {

    @Throws(SecRandomCopyException::class)
    internal abstract fun nextBytesCopyTo(size: Int, ptrBytes: CPointer<ByteVar>)

    internal companion object {
        // Default instance
        internal fun instance(): SecRandomDelegate
    }

    internal class Strong private constructor(): SecRandomDelegate {
        internal companion object {
            @Throws(NoSuchAlgorithmException::class)
            internal fun instance(): SecRandomDelegate
        }
    }
}

