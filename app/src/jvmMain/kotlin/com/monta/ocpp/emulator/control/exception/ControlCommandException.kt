package com.monta.ocpp.emulator.control.exception

class ControlCommandException(
    val code: String,
    message: String,
) : Exception(message)
