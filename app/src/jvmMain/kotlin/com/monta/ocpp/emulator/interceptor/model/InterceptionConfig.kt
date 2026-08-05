package com.monta.ocpp.emulator.interceptor.model

import androidx.compose.runtime.MutableState

class InterceptionConfig(
    var onRequest: MutableState<Interception>,
    var onResponse: MutableState<Interception>,
)
