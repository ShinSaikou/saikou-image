# saikou-image

Image utilities for Android manga readers: format sniffing from magic bytes, animation detection,
double-page splitting and rotation, reader background detection, and hardware bitmap limits. It is
what the [Saikou](https://github.com/ShinSaikou/saikou) reader and download engine use, published
on its own so other apps can use it too.

- Pure Kotlin format detection: no native decoder, so it runs in plain JVM unit tests.
- Works on [Okio](https://square.github.io/okio/) `BufferedSource`s and `InputStream`s.
- Android 8.0 (API 26) and up.

## Install

The library is published through [JitPack](https://jitpack.io/#ShinSaikou/saikou-image).

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.github.ShinSaikou:saikou-image:0.1.0")
}
```

## Use

```kotlin
import com.shinsaikou.image.ImageUtil
import com.shinsaikou.image.GLUtil

// What is this file? Reads at most the first 32 bytes.
val type: ImageUtil.ImageType? = ImageUtil.findImageType { file.inputStream() }
val isPicture = ImageUtil.isImage(file.name) { file.inputStream() }

// Animated WebP, GIF and the like, from a buffered page.
val animated = ImageUtil.isAnimatedAndSupported(source)

// A double page: one half of it, or both halves stacked.
if (ImageUtil.isWideImage(source)) {
    val right = ImageUtil.splitInHalf(source, ImageUtil.Side.RIGHT)
    val stacked = ImageUtil.splitAndMerge(source, ImageUtil.Side.RIGHT)
}
val turned = ImageUtil.rotateImage(source, degrees = 90f)

// The reader background (white, black or a gradient) that fits the page's edges.
val background = ImageUtil.chooseBackground(context, page.inputStream())

// Whether a page can be decoded as a hardware bitmap on this device.
ImageUtil.hardwareBitmapThreshold = GLUtil.DEVICE_TEXTURE_LIMIT
val hardware = ImageUtil.canUseHardwareBitmap(width, height)
```

`TallImageSplitCalculator` holds the rule for when a tall strip should be cut into parts, and into
how many.

## Develop

```bash
./gradlew :image:testDebugUnitTest
./gradlew :image:publishToMavenLocal
```

To try a change in an app before tagging it, include this build from the app's
`settings.gradle.kts` and point the published coordinates at it:

```kotlin
includeBuild("path/to/saikou-image") {
    dependencySubstitution {
        substitute(module("com.github.ShinSaikou:saikou-image")).using(project(":image"))
    }
}
```

Saikou does this when `saikou.image.dir` is set in its `local.properties`.

A release is a git tag (`0.1.0`, `0.2.0` and so on): JitPack builds the tag on its first request.
Record each one in [CHANGELOG.md](CHANGELOG.md).

## License

Apache License, Version 2.0; see [LICENSE](LICENSE). Part of the code comes from other
Apache-licensed projects, which [NOTICE](NOTICE) names.
