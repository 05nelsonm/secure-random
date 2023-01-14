# SecureRandom native app

Simple native application that will use `SecureRandom` to generate and 
then print random bytes.

### Running

On Linux:
```
./gradlew :samples:native:runDebugExecutableLinuxX64 -PKMP_TARGETS=LINUX_X64
```

On macOS X:
```
./gradlew :samples:native:runDebugExecutableMacosX64 -PKMP_TARGETS=MACOS_X64
```

On macOS M1-2:
```
./gradlew :samples:native:runDebugExecutableMacosArm64 -PKMP_TARGETS=MACOS_ARM64
```

On Windows:
```
./gradlew :samples:native:runDebugExecutableMingwX64 -PKMP_TARGETS=MINGW_X64
```
