plugins {
    kotlin("multiplatform") version "2.1.10" apply false
    id("org.jetbrains.compose") version "1.7.3" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10" apply false
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1" apply false
    id("com.google.devtools.ksp") version "2.1.10-1.0.31" apply false
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
