package com.monta.ocpp.emulator.platform.util

import java.io.File

val appRoot by lazy {
    File(
        buildString {
            append(System.getProperty("user.home"))
            append(File.separator)
            append("monta")
            append(File.separator)
        },
    )
}
