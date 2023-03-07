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
package io.matthewnelson.secure.random

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
@Suppress("NOTHING_TO_INLINE")
internal inline fun ByteArray?.ifNotNullOrEmpty(block: ByteArray.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }

    if (this == null || this.isEmpty()) return
    block.invoke(this)
}

@Deprecated(
    message = """
        Project moved to the https://github.com/KotlinCrypto organization.
        
        Step 1: Add dependency 'org.kotlincrypto:secure-random:0.1.0' to your project.
        Step 2: Use the 'ReplaceWith' feature on import.
        Step 3: Remove dependency 'io.matthewnelson.kotlin-components:secure-random'.
        
        See more at https://github.com/05nelsonm/secure-random/blob/master/README.md#migration
    """,
    replaceWith = ReplaceWith(
        expression = "SecRandomCopyException",
        imports = [ "org.kotlincrypto.SecRandomCopyException" ]
    ),
    level = DeprecationLevel.WARNING,
)
public class SecRandomCopyException: RuntimeException {
    public constructor(): super()
    public constructor(message: String?): super(message)
    public constructor(message: String?, cause: Throwable?): super(message, cause)
    public constructor(cause: Throwable?): super(cause)
}
