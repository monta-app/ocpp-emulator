plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
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
                implementation(project.dependencies.platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.8.0"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")

                // Websocket Client
                implementation(project.dependencies.platform("io.ktor:ktor-bom:2.3.10"))
                implementation("io.ktor:ktor-client-core")
                implementation("io.ktor:ktor-client-cio")
                implementation("io.ktor:ktor-client-websockets")
                implementation("io.ktor:ktor-client-logging")
                implementation("io.ktor:ktor-client-content-negotiation")
                implementation("io.ktor:ktor-serialization-jackson")

                // Jackson
                implementation(project.dependencies.platform("com.fasterxml.jackson:jackson-bom:2.17.0"))
                implementation("com.fasterxml.jackson.core:jackson-core")
                implementation("com.fasterxml.jackson.core:jackson-annotations")
                implementation("com.fasterxml.jackson.core:jackson-databind")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
                implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
                implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")

                // QR Code Library
                implementation("io.nayuki:qrcodegen:1.8.0")

                // Bouncy Castle for Eichrecht signed data
                implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")

                // Logging
                implementation("ch.qos.logback:logback-classic:1.5.6")
                implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

                // Sentry (Crash reporting)
                implementation(project.dependencies.platform("io.sentry:sentry-bom:7.8.0"))
                implementation("io.sentry:sentry")
                implementation("io.sentry:sentry-logback")

                // Markdown
                implementation("net.swiftzer.semver:semver:2.0.0")

                // Dependency Injection
                implementation(project.dependencies.platform("io.insert-koin:koin-bom:3.5.6"))
                implementation("io.insert-koin:koin-core")
                implementation("io.insert-koin:koin-ktor")
                implementation("io.insert-koin:koin-logger-slf4j")
                implementation("io.insert-koin:koin-annotations:1.3.1")

                // SQL Database
                implementation(project.dependencies.platform("org.jetbrains.exposed:exposed-bom:0.49.0"))
                implementation("org.jetbrains.exposed:exposed-core")
                implementation("org.jetbrains.exposed:exposed-dao")
                implementation("org.jetbrains.exposed:exposed-jdbc")
                implementation("org.jetbrains.exposed:exposed-java-time")

                // Data Source Connection Pool
                implementation("com.zaxxer:HikariCP:5.1.0")

                // SQLite JDBC Driver
                implementation(files("libs/sqlite-jdbc-3.42.0.0.jar"))
            }
        }
    }
}

dependencies {
    add("kspJvm", "io.insert-koin:koin-ksp-compiler:1.3.1")
}

ktlint {
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}
