package com.monta.ocpp.emulator.v16.service.interceptor

import androidx.compose.runtime.MutableState

class InterceptionConfig(
    var onRequest: MutableState<Interception>,
    var onResponse: MutableState<Interception>
)
