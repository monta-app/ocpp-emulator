package com.monta.ocpp.emulator.interceptor.model

import kotlinx.coroutines.flow.MutableStateFlow

class InterceptionConfig(
    var onRequest: MutableStateFlow<Interception>,
    var onResponse: MutableStateFlow<Interception>,
)
