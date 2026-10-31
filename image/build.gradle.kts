import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
}

android {
    namespace = "com.shinsaikou.image"
    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.sdk.min.get().toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.java.get())
    }

    testOptions {
        // ImageUtil reads Build fields and platform statics at class init. Returning defaults keeps
        // the pure-Kotlin halves (format sniffing, the split arithmetic) testable on the JVM.
        unitTests.isReturnDefaultValues = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
    }
}

dependencies {
    api(libs.okio)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit.jupiter)
    testImplementation(libs.kotest.assertions)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "saikou-image"
            afterEvaluate { from(components["release"]) }
        }
    }
}
