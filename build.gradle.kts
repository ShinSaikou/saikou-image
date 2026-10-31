buildscript {
    dependencies {
        // Pins the Kotlin Gradle plugin that AGP's built-in Kotlin uses.
        classpath(libs.kotlin.gradle)
    }
}

plugins {
    alias(libs.plugins.android.library) apply false
}

allprojects {
    // JitPack publishes the one artifact of this build as com.github.ShinSaikou:saikou-image:<tag>;
    // the local publication uses the same coordinates.
    group = "com.github.ShinSaikou"
    version = providers.gradleProperty("version").orElse("0.1.0").get()
}
