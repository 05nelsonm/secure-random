# secure-random
[![badge-license]][url-license]
[![badge-latest-release]][url-latest-release]

[![badge-kotlin]][url-kotlin]

![badge-platform-android]
![badge-platform-jvm]
![badge-platform-js]
![badge-platform-js-node]
![badge-platform-linux]
![badge-platform-macos]
![badge-platform-ios]
![badge-platform-tvos]
![badge-platform-watchos]
![badge-platform-windows]
![badge-support-android-native]
![badge-support-apple-silicon]
![badge-support-js-ir]

<!--
![badge-platform-wasm]
-->

Kotlin Multiplatform library for obtaining cryptographically 
secure random data from the system. Modeled after Java's `SecureRandom` 
class, it provides a simple API surface area. Under the hood it 
utilizes system functions so that `SecureRandom` is accessible from 
common code. 

Heavily inspired by the [rust-random/getrandom][url-rust-random] crate 
for the native Linux/Android implementation.

A full list of `kotlin-components` projects can be found [HERE][url-kotlin-components]

### Example Usages

```kotlin
fun main() {
    val sRandom = SecureRandom()

    val bytes: ByteArray = try {
        sRandom.nextBytesOf(count = 20)
    } catch (e: SecRandomCopyException) {
        e.printStackTrace()
        return
    }

    println(bytes.toList())
}
```

```kotlin
fun main() {
    val sRandom = SecureRandom()
    val bytes = ByteArray(20)
    
    try {
        sRandom.nextBytesCopyTo(bytes)
    } catch (e: SecRandomCopyException) {
        e.printStackTrace()
        return
    }

    println(bytes.toList())
}
```

### Samples

See the [native sample](samples/native/README.md) 

### Get Started

<!-- TAG_VERSION -->

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.matthewnelson.kotlin-components:secure-random:0.1.2")
}
```

<!-- TAG_VERSION -->

```groovy
// build.gradle
dependencies {
    implementation "io.matthewnelson.kotlin-components:secure-random:0.1.2"
}
```

### Kotlin Version Compatibility

<!-- TAG_VERSION -->

| secure-random | kotlin |
|:-------------:|:------:|
|     0.1.2     | 1.8.0  |
|     0.1.1     | 1.8.0  |
|     0.1.0     | 1.8.0  |

### Git

This project utilizes git submodules. You will need to initialize them when
cloning the repository via:

```bash
$ git clone --recursive https://github.com/05nelsonm/secure-random.git
```

If you've already cloned the repository, run:
```bash
$ git checkout master
$ git pull
$ git submodule update --init
```

In order to keep submodules updated when pulling the latest code, run:
```bash
$ git pull --recurse-submodules
```

<!-- TAG_VERSION -->
[badge-latest-release]: https://img.shields.io/badge/latest--release-0.1.2-blue.svg?style=flat
[badge-license]: https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat

<!-- TAG_DEPENDENCIES -->
[badge-kotlin]: https://img.shields.io/badge/kotlin-1.8.0-blue.svg?logo=kotlin

<!-- TAG_PLATFORMS -->
[badge-platform-android]: http://img.shields.io/badge/-android-6EDB8D.svg?style=flat
[badge-platform-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg?style=flat
[badge-platform-js]: http://img.shields.io/badge/-js-F8DB5D.svg?style=flat
[badge-platform-js-node]: https://img.shields.io/badge/-nodejs-68a063.svg?style=flat
[badge-platform-linux]: http://img.shields.io/badge/-linux-2D3F6C.svg?style=flat
[badge-platform-macos]: http://img.shields.io/badge/-macos-111111.svg?style=flat
[badge-platform-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg?style=flat
[badge-platform-tvos]: http://img.shields.io/badge/-tvos-808080.svg?style=flat
[badge-platform-watchos]: http://img.shields.io/badge/-watchos-C0C0C0.svg?style=flat
[badge-platform-wasm]: https://img.shields.io/badge/-wasm-624FE8.svg?style=flat
[badge-platform-windows]: http://img.shields.io/badge/-windows-4D76CD.svg?style=flat
[badge-support-android-native]: http://img.shields.io/badge/support-[AndroidNative]-6EDB8D.svg?style=flat
[badge-support-apple-silicon]: http://img.shields.io/badge/support-[AppleSilicon]-43BBFF.svg?style=flat
[badge-support-js-ir]: https://img.shields.io/badge/support-[js--IR]-AAC4E0.svg?style=flat

[url-latest-release]: https://github.com/05nelsonm/secure-random/releases/latest
[url-license]: https://www.apache.org/licenses/LICENSE-2.0.txt
[url-kotlin]: https://kotlinlang.org
[url-kotlin-components]: https://kotlin-components.matthewnelson.io
[url-rust-random]: https://github.com/rust-random/getrandom
