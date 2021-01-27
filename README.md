# gradle-publication-configuration
Demonstrates challenges related to creating and configuring a publication in separate steps

## Setup
1. `./gradlew :lib:generateDescriptorFileForFooPublication`
2. `less lib/build/publications/foo/ivy.xml`

This example successfully creates a publication (from the built-in `java` component) in a binary plugin,
then configures the publication in build script.  A custom artifact and configuration are added to the ivy descriptor.

Expected output of `lib/build/publications/foo/ivy.xml`:
```
<?xml version="1.0" encoding="UTF-8"?>
<ivy-module version="2.0">
  <info organisation="gradle-publication-configuration" module="lib" revision="unspecified" status="integration" publication="20210127103623"/>
  <configurations>
    <conf name="compile" visibility="public"/>
    <conf name="default" visibility="public" extends="runtime"/>
    <conf name="foo-conf" visibility="public"/>
    <conf name="runtime" visibility="public"/>
  </configurations>
  <publications>
    <artifact name="lib" type="jar" ext="jar" conf="compile,runtime"/>
    <artifact name="some-generated-file" type="foo-file" ext="xml" conf="foo-conf"/>
  </publications>
  <dependencies>
    <dependency org="org.apache.commons" name="commons-math3" rev="3.6.1" conf="compile-&gt;default"/>
    <dependency org="com.google.guava" name="guava" rev="29.0-jre" conf="runtime-&gt;default"/>
    <dependency org="org.apache.commons" name="commons-math3" rev="3.6.1" conf="runtime-&gt;default"/>
  </dependencies>
</ivy-module>
```

# Steps to reproduce issue
1. Navigate to `buildSrc/src/main/java/com/kylemoore/foo/CreatesFooPublicationPlugin.java:24`
and uncomment the method invocation `configureFooPublication(fooPublication);`
2. Repeat setup steps above.

Expected contents of `lib/build/publications/foo/ivy.xml`:
```
<?xml version="1.0" encoding="UTF-8"?>
<ivy-module version="2.0">
  <info organisation="gradle-publication-configuration" module="lib" revision="unspecified" status="integration" publication="20210127103623"/>
  <configurations>
    <conf name="compile" visibility="public"/>
    <conf name="default" visibility="public" extends="runtime"/>
+   <conf name="docs" visibility="public"/>
    <conf name="foo-conf" visibility="public"/>
    <conf name="runtime" visibility="public"/>
  </configurations>
  <publications>
    <artifact name="lib" type="jar" ext="jar" conf="compile,runtime"/>
    <artifact name="some-generated-file" type="foo-file" ext="xml" conf="foo-conf"/>
  </publications>
  <dependencies>
    <dependency org="org.apache.commons" name="commons-math3" rev="3.6.1" conf="compile-&gt;default"/>
    <dependency org="com.google.guava" name="guava" rev="29.0-jre" conf="runtime-&gt;default"/>
    <dependency org="org.apache.commons" name="commons-math3" rev="3.6.1" conf="runtime-&gt;default"/>
  </dependencies>
</ivy-module>
```

Actual contents of `lib/build/publications/foo/ivy.xml`:
```
<?xml version="1.0" encoding="UTF-8"?>
<ivy-module version="2.0">
  <info organisation="gradle-publication-configuration" module="lib" revision="unspecified" status="integration" publication="20210127104037"/>
  <configurations>
    <conf name="compile" visibility="public"/>
    <conf name="default" visibility="public" extends="runtime"/>
    <conf name="docs" visibility="public"/>
    <conf name="foo-conf" visibility="public"/>
    <conf name="runtime" visibility="public"/>
  </configurations>
  <publications>
    <artifact name="lib" type="jar" ext="jar" conf="compile,runtime"/>
    <artifact name="some-generated-file" type="foo-file" ext="xml" conf="foo-conf"/>
  </publications>
  <dependencies/>
</ivy-module>
```

Note that the dependencies element is now **empty**.