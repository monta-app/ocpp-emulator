import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.google.devtools.ksp")
}

group = "com.monta.ocpp.emulator.v201"
version = "2.3.0"

kotlin {
    jvmToolchain(17)
    jvm {
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            // Adds ksp sources so that Intellij can recognize the generated sources (hacky)
            sourceSets.getByName("jvmMain").kotlin.srcDir("build/generated/ksp/jvm/jvmMain/kotlin")
            // The following is required for the above not to break klint
            tasks.named("runKtlintFormatOverJvmMainSourceSet").configure { dependsOn("kspKotlinJvm") }
            // Dependencies
            dependencies {

                implementation(project(":common"))

                implementation(compose.desktop.currentOs)
                implementation(compose.material3)

                // OCPP Libs
                implementation("com.github.monta-app.library-ocpp:ocpp-core:1.0.4")
                implementation("com.github.monta-app.library-ocpp:ocpp-v201:1.0.4")

                // Coroutines
                implementation(project.dependencies.platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.1"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")

                // Websocket Client
                implementation(project.dependencies.platform("io.ktor:ktor-bom:3.1.1"))
                implementation("io.ktor:ktor-client-core")
                implementation("io.ktor:ktor-client-cio")
                implementation("io.ktor:ktor-client-websockets")
                implementation("io.ktor:ktor-client-logging")
                implementation("io.ktor:ktor-client-content-negotiation")
                implementation("io.ktor:ktor-serialization-jackson")

                // Jackson
                implementation(project.dependencies.platform("com.fasterxml.jackson:jackson-bom:2.18.3"))
                implementation("com.fasterxml.jackson.core:jackson-core")
                implementation("com.fasterxml.jackson.core:jackson-annotations")
                implementation("com.fasterxml.jackson.core:jackson-databind")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
                implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

                // Logging
                implementation("ch.qos.logback:logback-classic:1.5.18")
                implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

                // Sentry (Crash reporting)
                implementation(project.dependencies.platform("io.sentry:sentry-bom:8.5.0"))
                implementation("io.sentry:sentry")
                implementation("io.sentry:sentry-logback")

                // Markdown
                implementation("net.swiftzer.semver:semver:2.1.0")

                // Dependency Injection
                implementation(project.dependencies.platform("io.insert-koin:koin-bom:4.0.2"))
                implementation("io.insert-koin:koin-core")
                implementation("io.insert-koin:koin-ktor")
                implementation("io.insert-koin:koin-logger-slf4j")
                implementation("io.insert-koin:koin-annotations:2.0.0")

                // SQL Database
                implementation(project.dependencies.platform("org.jetbrains.exposed:exposed-bom:0.60.0"))
                implementation("org.jetbrains.exposed:exposed-core")
                implementation("org.jetbrains.exposed:exposed-dao")
                implementation("org.jetbrains.exposed:exposed-jdbc")
                implementation("org.jetbrains.exposed:exposed-java-time")
            }
        }
    }
}

dependencies {
    add("kspJvm", "io.insert-koin:koin-ksp-compiler:2.0.0")
}

ktlint {
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

compose.desktop {
    application {

        buildTypes.release.proguard {
            configurationFiles.from(project.file("compose-desktop.pro"))
        }

        mainClass = "com.monta.ocpp.emulator.v201.AppKt"

        nativeDistributions {
            modules("java.naming", "java.instrument", "java.management", "java.sql", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.Exe)

            packageName = "OcppEmulator"
            packageVersion = "$version"

            macOS {
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
                bundleID = "com.monta.ocpp.emulator.v201"
            }
            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
            }
        }
    }
}
