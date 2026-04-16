plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

group = "com.monta.ocpp.emulator.common"
version = "2.2.0"

kotlin {
    jvm()
    sourceSets {
        jvmTest.dependencies {
            implementation(libs.kotlin.test.junit5)
            implementation(libs.junit.jupiter)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)

            // Material Icons
            implementation(compose.materialIconsExtended)

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

            // Markdown
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

dependencies {
    add("kspJvm", libs.koin.ksp.compiler)
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}

// Alias for shared CI workflow which runs `:common:test`
tasks.register("test") {
    dependsOn("jvmTest")
}

ktlint {
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}
