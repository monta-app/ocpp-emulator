plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jlleitschuh.gradle.ktlint")
    id("com.google.devtools.ksp")
}

group = "com.monta.ocpp.emulator.common"
version = "2.2.0"

kotlin {
    jvmToolchain(17)
    jvm {
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            // Dependencies
            dependencies {

                implementation(compose.desktop.currentOs)

                // Coroutines
                implementation(project.dependencies.platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")

                // Websocket Client
                implementation(project.dependencies.platform("io.ktor:ktor-bom:3.1.3"))
                implementation("io.ktor:ktor-client-core")
                implementation("io.ktor:ktor-client-cio")
                implementation("io.ktor:ktor-client-websockets")
                implementation("io.ktor:ktor-client-logging")
                implementation("io.ktor:ktor-client-content-negotiation")
                implementation("io.ktor:ktor-serialization-jackson")

                // Jackson
                implementation(project.dependencies.platform("com.fasterxml.jackson:jackson-bom:2.19.0"))
                implementation("com.fasterxml.jackson.core:jackson-core")
                implementation("com.fasterxml.jackson.core:jackson-annotations")
                implementation("com.fasterxml.jackson.core:jackson-databind")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
                implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

                // QR Code Library
                implementation("io.nayuki:qrcodegen:1.8.0")

                // Bouncy Castle for Eichrecht signed data
                implementation("org.bouncycastle:bcprov-jdk18on:1.80")

                // Logging
                implementation("ch.qos.logback:logback-classic:1.5.18")
                implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")

                // Sentry (Crash reporting)
                implementation(project.dependencies.platform("io.sentry:sentry-bom:8.12.0"))
                implementation("io.sentry:sentry")
                implementation("io.sentry:sentry-logback")

                // Markdown
                implementation("net.swiftzer.semver:semver:2.1.0")

                // Dependency Injection
                implementation(project.dependencies.platform("io.insert-koin:koin-bom:4.0.4"))
                implementation("io.insert-koin:koin-core")
                implementation("io.insert-koin:koin-ktor")
                implementation("io.insert-koin:koin-logger-slf4j")
                implementation("io.insert-koin:koin-annotations:2.0.0")

                // SQL Database
                implementation(project.dependencies.platform("org.jetbrains.exposed:exposed-bom:0.61.0"))
                implementation("org.jetbrains.exposed:exposed-core")
                implementation("org.jetbrains.exposed:exposed-dao")
                implementation("org.jetbrains.exposed:exposed-jdbc")
                implementation("org.jetbrains.exposed:exposed-java-time")

                // Data Source Connection Pool
                implementation("com.zaxxer:HikariCP:6.3.0")

                // SQLite JDBC Driver
                implementation(files("libs/sqlite-jdbc-3.42.0.0.jar"))
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
