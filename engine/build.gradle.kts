plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.koin.compiler)
    alias(libs.plugins.kover)
}

group = "com.monta.ocpp.emulator"
version = "2.6.0"

kotlin {
    jvmToolchain(25)
    jvm()
    sourceSets {
        jvmTest.dependencies {
            implementation(libs.bundles.kotest)
        }
        jvmMain.dependencies {
            // OCPP Libs — exposed as `api` because the engine's public surface (EmulatorEngine DTOs
            // and the raw-message command) leaks OCPP-library enum/message types to callers.
            api(libs.ocpp.core)
            api(libs.ocpp.v16)
            // ocpp-library exposes Jackson 3's JsonNode in its API without an api-scope dependency
            implementation(libs.jackson3.databind)

            // Coroutines
            implementation(project.dependencies.platform(libs.kotlinx.coroutines.bom))
            implementation(libs.bundles.coroutines)

            // Websocket Client
            implementation(project.dependencies.platform(libs.ktor.bom))
            implementation(libs.bundles.ktor.client)

            // Jackson
            implementation(project.dependencies.platform(libs.jackson.bom))
            implementation(libs.bundles.jackson)

            // QR Code Library
            implementation(libs.qrcodegen)

            // Bouncy Castle for Eichrecht signed data
            implementation(libs.bouncy.castle)

            // Logging
            implementation(libs.bundles.logging)

            // Sentry (Crash reporting)
            implementation(project.dependencies.platform(libs.sentry.bom))
            implementation(libs.bundles.sentry)

            // Semver
            implementation(libs.semver)

            // Dependency Injection
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.bundles.koin)

            // SQL Database
            implementation(project.dependencies.platform(libs.exposed.bom))
            implementation(libs.bundles.exposed)

            // Data Source Connection Pool
            implementation(libs.hikaricp)

            // SQLite JDBC Driver
            implementation(files("libs/sqlite-jdbc-3.42.0.0.jar"))
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}

// Alias mirroring :app so the shared CI workflow can run `:engine:test`
tasks.register("test") {
    dependsOn("jvmTest")
}

ktlint {
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}
