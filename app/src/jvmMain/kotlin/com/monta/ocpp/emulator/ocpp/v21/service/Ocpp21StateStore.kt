package com.monta.ocpp.emulator.ocpp.v21.service

import com.monta.library.ocpp.common.profile.OcppRequest
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton

@Singleton
class Ocpp21StateStore {
    private val latest = ConcurrentHashMap<String, OcppRequest>()
    private val tariffs = ConcurrentHashMap<Long, MutableMap<String, Any>>()
    private val derControls = ConcurrentHashMap<Long, MutableMap<String, Any>>()
    private val streams = ConcurrentHashMap<Long, MutableMap<Int, Any>>()

    fun record(
        action: String,
        request: OcppRequest,
    ) {
        latest[action] = request
    }

    fun latest(
        action: String,
    ): OcppRequest? = latest[action]

    fun tariffs(
        chargePointId: Long,
    ): MutableMap<String, Any> = tariffs.getOrPut(chargePointId) { ConcurrentHashMap() }

    fun derControls(
        chargePointId: Long,
    ): MutableMap<String, Any> = derControls.getOrPut(chargePointId) { ConcurrentHashMap() }

    fun streams(
        chargePointId: Long,
    ): MutableMap<Int, Any> = streams.getOrPut(chargePointId) { ConcurrentHashMap() }
}
