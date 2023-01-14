import io.matthewnelson.kotlin.components.kmp.KmpTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
plugins {
    id(pluginId.kmp.configuration)
}

kmpConfiguration {

    fun KotlinNativeTarget.setup() {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    val osName = System.getProperty("os.name")
    when {
        osName.startsWith("Windows", true) -> {
            KmpTarget.NonJvm.Native.Mingw.X64(target = { setup() })
        }
        osName == "Mac OS X" -> {
            KmpTarget.NonJvm.Native.Unix.Darwin.Macos.X64(target = { setup() })
        }
        osName.contains("Mac", true) -> {
            KmpTarget.NonJvm.Native.Unix.Darwin.Macos.Arm64(target = { setup() })
        }
        osName == "Linux" -> {
            KmpTarget.NonJvm.Native.Unix.Linux.X64(target = { setup() })
        }
        else -> null
    }?.let { target ->
        setupMultiplatform(
            targets = setOf(target),
            commonMainSourceSet = {
                dependencies {
                    implementation(project(":secure-random"))
                }
            }
        )
    }
}
