# CHANGELOG

## Version 0.1.3 (2023-03-07)
 - Adds `Deprecated` annotations to `SecureRandom` and `SecRandomCopyException`
 - Moves the project to [KotlinCrypto][url-1] organization at [THIS][url-2] 
   repository.

## Version 0.1.2 (2023-01-16)
 - Add support for `js-browser` and `js-node` ([#41][pr-41])

## Version 0.1.1 (2023-01-16)
 - Fixes `fillCompletely` function not updating internal index properly, 
   resulting in an endless loop if more than 2 invocations were needed. ([#40][pr-40])

## Version 0.1.0 (2023-01-16)
 - Initial Release

[url-1]: https://github.com/KotlinCrypto/
[url-2]: https://github.com/KotlinCrypto/secure-random
[pr-40]: https://github.com/05nelsonm/secure-random/pull/40
[pr-41]: https://github.com/05nelsonm/secure-random/pull/41
