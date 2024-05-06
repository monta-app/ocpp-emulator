package com.monta.ocpp.emulator.interceptor

import androidx.compose.runtime.MutableState

class InterceptionConfig(
    var onRequest: MutableState<Interception>,
    var onResponse: MutableState<Interception>
)
