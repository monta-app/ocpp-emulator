plugins {
    kotlin("multiplatform") version "2.0.20" apply false
    id("org.jetbrains.compose") version "1.6.11" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1" apply false
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
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
