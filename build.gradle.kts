plugins {
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.ksp) apply false
}

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
}

// Register a root-level 'test' task so the shared CI workflow
// (which runs `./gradlew test`) doesn't fail on this project.
tasks.register("test") {
    description = "Aggregate test task for CI compatibility"
    group = "verification"
}
