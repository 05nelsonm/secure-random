# secure-random

This project has been moved to the [KotlinCrypto][1] organization 
and can be found [HERE][2]. No further updates to this project 
will be made, and it **should not** be used.

### Migration

If you are already utilizing this project, you can follow the steps below 
to migrate to [KotlinCrypto/secure-random][2]

1. Add the replacement dependency + update this project's dependency to the 
   latest release (to bring in the `Deprecation` notice).
   ```kotlin
   dependencies {
       implementation("org.kotlincrypto:secure-random:0.1.0")
   
       // TODO: Remove after replacing imports in source code
       implementation("io.matthewnelson.kotlin-components:secure-random:0.1.3")
   }
   ```
2. Use the `ReplaceWith` deprecation feature to replace imports for `SecureRandom` and `SecRandomCopyException`

   ![image](https://user-images.githubusercontent.com/44778092/223445774-3dd67218-9d6e-446e-be80-643b4d0282db.png)

3. Remove dependency `io.matthewnelson.kotlin-components:secure-random` from your project.

[1]: https://github.com/KotlinCrypto/
[2]: https://github.com/KotlinCrypto/secure-random
