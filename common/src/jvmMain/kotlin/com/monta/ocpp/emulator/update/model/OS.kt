package com.monta.ocpp.emulator.update.model

internal enum class OS(
    vararg val fileFormat: String
) {
    MacOS("dmg", "pkg"),
    Linux("deb", "rpm"),
    Windows("exe", "msi")
}
