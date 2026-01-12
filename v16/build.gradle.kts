import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.ksp)
}

group = "com.monta.ocpp.emulator"
version = "2.4.0"

kotlin {
    jvm()
    sourceSets {
        jvmMain.dependencies {
            implementation(project(":common"))

            implementation(compose.desktop.currentOs)

            // Material Icons
            implementation(compose.materialIconsExtended)

            // OCPP Libs
            implementation(libs.ocpp.core)
            implementation(libs.ocpp.v16)

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
        }
    }
}

dependencies {
    add("kspJvm", libs.koin.ksp.compiler)
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
                System.getenv("SENTRY_DSN")?.let { "-Dsentry.dsn=$it" }
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
