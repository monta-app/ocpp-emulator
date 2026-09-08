import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
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
        jvmMain.dependencies {
            // The headless emulation engine (OCPP protocol, domain services/repos/entities, DB).
            implementation(project(":engine"))

            implementation(compose.desktop.currentOs)

            // Material Icons
            implementation(libs.compose.material.icons.extended)

            // Compose resources (classpath SVG loading)
            implementation(libs.compose.components.resources)

            // Navigation (type-safe routes are @Serializable, hence the serialization plugin above)
            implementation(libs.androidx.navigation.compose)

            // OCPP Libs — the UI still constructs and sends OCPP messages directly (SendMessageWindow,
            // the connection button, etc.), so it depends on the OCPP libraries alongside :engine.
            implementation(libs.ocpp.core)
            implementation(libs.ocpp.v16)
            // ocpp-library exposes Jackson 3's JsonNode in its API without an api-scope dependency
            implementation(libs.jackson3.databind)

            // Coroutines
            implementation(project.dependencies.platform(libs.kotlinx.coroutines.bom))
            implementation(libs.bundles.coroutines)

            // Websocket Client (self-updater in platform/update fetches GitHub releases over HTTP)
            implementation(project.dependencies.platform(libs.ktor.bom))
            implementation(libs.bundles.ktor.client)

            // Jackson
            implementation(project.dependencies.platform(libs.jackson.bom))
            implementation(libs.bundles.jackson)

            // QR Code Library
            implementation(libs.qrcodegen)

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

            // SQL Database — the UI still opens Exposed `transaction {}` blocks and reads DAOs
            // directly, so it depends on Exposed alongside :engine.
            implementation(project.dependencies.platform(libs.exposed.bom))
            implementation(libs.bundles.exposed)
        }
    }
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

        mainClass = "com.monta.ocpp.emulator.AppKt"

        nativeDistributions {
            modules("java.naming", "java.instrument", "java.management", "java.sql", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Deb, TargetFormat.Rpm, TargetFormat.Exe)

            packageName = "OcppEmulator"
            packageVersion = "$version"

            // JVM arguments, pass build-time properties here
            jvmArgs += listOfNotNull(
                System.getenv("SENTRY_DSN")?.let { "-Dsentry.dsn=$it" },
            )

            macOS {
                iconFile.set(project.file("src/jvmMain/resources/icon.icns"))
                bundleID = "com.monta.ocpp.emulator.v16"
            }
            linux {
                iconFile.set(project.file("src/jvmMain/resources/icon.png"))
            }
        }
    }
}
