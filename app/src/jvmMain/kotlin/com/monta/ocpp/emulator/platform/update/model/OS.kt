package com.monta.ocpp.emulator.platform.update.model

internal enum class OS(
    vararg val fileFormat: String,
) {
    MacOS("dmg", "pkg"),
    Linux("deb", "rpm"),
    Windows("exe", "msi"),
}
