# gradle-publication-configuration
Demonstrates challenges related to creating and configuring a publication in separate steps

## Steps to reproduce
1. `./gradlew :lib:generateDescriptorFileForFooPublication`
2. `less lib/build/publications/foo/ivy.xml`

This example successfully creates a publication (from the built-in `java` component) in a binary plugin,
then configures the publication in a build script.  A custom artifact and configuration are added to the ivy descriptor.