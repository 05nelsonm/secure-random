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
import io.matthewnelson.kotlin.components.kmp.KmpTarget
import io.matthewnelson.kotlin.components.kmp.util.*
import org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType

plugins {
    id(pluginId.kmp.configuration)
    id(pluginId.kmp.publish)
}

kmpConfiguration {
    setupMultiplatform(targets=
        setOf(
            KmpTarget.Jvm.Android(
                buildTools = versions.android.buildTools,
                compileSdk = versions.android.sdkCompile,

                // https://android-developers.googleblog.com/2013/08/some-securerandom-thoughts.html
                minSdk = versions.android.sdkMin19, // KitKat (4.4)

                namespace = "io.matthewnelson.secure.random",
                compileSourceOption = JavaVersion.VERSION_1_8,
                compileTargetOption = JavaVersion.VERSION_1_8,
                kotlinJvmTarget = JavaVersion.VERSION_1_8,
                target = {
                    publishLibraryVariants("release")
                }
            ),
            KmpTarget.Jvm.Jvm(kotlinJvmTarget = JavaVersion.VERSION_1_8),

            // TODO: Implement (Issue #37)
            //  Also uncomment in :tools:check-publication build.gradle.kts
//            KmpTarget.NonJvm.JS(
//                compilerType = KotlinJsCompilerType.BOTH,
//                browser = null,
//                node = KmpTarget.NonJvm.JS.Node()
//            ),

            KmpTarget.NonJvm.Native.Unix.Darwin.Watchos.DeviceArm64.DEFAULT,
        ) +
        KmpTarget.NonJvm.Native.Android.ALL_DEFAULT             +

        // TODO: Implement (Issue #36)
        //  Also uncomment in :tools:check-publication build.gradle.kts
//        KmpTarget.NonJvm.Native.Wasm.ALL_DEFAULT                +

        KmpTarget.NonJvm.Native.Unix.Darwin.Ios.ALL_DEFAULT     +
        KmpTarget.NonJvm.Native.Unix.Darwin.Macos.ALL_DEFAULT   +
        KmpTarget.NonJvm.Native.Unix.Darwin.Tvos.ALL_DEFAULT    +
        KmpTarget.NonJvm.Native.Unix.Darwin.Watchos.ALL_DEFAULT +
        KmpTarget.NonJvm.Native.Unix.Linux.ALL_DEFAULT          +
        KmpTarget.NonJvm.Native.Mingw.ALL_DEFAULT,

        commonTestSourceSet = {
            dependencies {
                implementation(kotlin("test"))
            }
        },

        kotlin = {
            explicitApi()

            val linuxMain = sourceSetLinuxMain
            val androidNativeMain = sourceSetAndroidNativeMain

            // If either linux or androidNative sources are available
            if (linuxMain != null || androidNativeMain != null) {
                sourceSets {
                    val linuxAndroidMain by creating {
                        dependsOn(sourceSetNativeMain!!)
                    }
                    val linuxAndroidTest by creating {
                        dependsOn(sourceSetNativeTest!!)
                    }

                    linuxMain?.apply {
                        dependsOn(linuxAndroidMain)
                    }
                    sourceSetLinuxTest {
                        dependsOn(linuxAndroidTest)
                    }
                    androidNativeMain?.apply {
                        dependsOn(linuxAndroidMain)
                    }
                    sourceSetAndroidNativeTest {
                        dependsOn(linuxAndroidTest)
                    }
                }
            }

            sourceSetLinuxX64Test {
                dependencies {
                    implementation(depsTest.kotlin.coroutines)
                }
            }
        }
    )
}

kmpPublish {
    setupModule(
        pomDescription = "Kotlin Components' SecureRandom Component",
    )
}
