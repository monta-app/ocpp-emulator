package com.monta.ocpp.emulator.chargepoint.devicemodel.service

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton

@Singleton
class DisplayMessageService {
    private val store = ConcurrentHashMap<Long, ConcurrentHashMap<Long, Any>>()

    fun set(
        chargePointId: Long,
        id: Long,
        message: Any,
    ) {
        store.getOrPut(chargePointId) { ConcurrentHashMap() }[id] = message
    }

    fun clear(
        chargePointId: Long,
        id: Long,
    ): Boolean = store[chargePointId]?.remove(id) != null

    fun getAll(
        chargePointId: Long,
        ids: List<Long>? = null,
    ): List<Any> {
        val messages = store[chargePointId]?.values.orEmpty()
        return if (ids == null) {
            messages.toList()
        } else {
            store[chargePointId]?.filterKeys { it in ids }?.values?.toList().orEmpty()
        }
    }
}
