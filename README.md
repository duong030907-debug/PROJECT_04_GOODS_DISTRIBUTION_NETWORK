# PROJECT_04_GOODS_DISTRIBUTION_NETWORK

## Dependencies

Third-party jars are vendored in `lib/` because this is a plain Eclipse Java project
(no Maven/Gradle build):

| Jar | Version | Module name |
| --- | --- | --- |
| `lib/guava-33.4.8-jre.jar` | Guava 33.4.8-jre | `com.google.common` |
| `lib/failureaccess-1.0.3.jar` | Guava `failureaccess` (runtime dependency of Guava) | `com.google.common.util.concurrent.internal` |
| `lib/error_prone_annotations-2.36.0.jar` | Error Prone annotations (compile-only, silences warnings) | `com.google.errorprone.annotations` |
| `lib/j2objc-annotations-3.0.0.jar` | J2ObjC annotations (compile-only) | `com.google.j2objc.annotations` |
| `lib/jspecify-1.0.0.jar` | JSpecify nullness annotations (compile-only) | `org.jspecify` |

All jars are declared on the **module path** in `.classpath`, and the project
descriptor requires Guava in `src/module-info.java`:

```java
module PROJECT_04_GOODS_DISTRIBUTION_NETWORK {
    requires com.google.common;
}
```

Use Guava anywhere in `src` without extra setup, e.g.:

```java
import com.google.common.collect.ImmutableList;
import com.google.common.base.Preconditions;
```

To upgrade Guava, drop the new jars into `lib/`, update the `path` entries in
`.classpath`, and keep `failureaccess` in sync with the version Guava declares.
