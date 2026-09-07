package com.monta.ocpp.emulator.chargepoint.devicemodel.service

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Singleton

data class DeviceVariable(
    val componentName: String,
    val componentInstance: String? = null,
    val variableName: String,
    val variableInstance: String? = null,
    val value: String,
    val readonly: Boolean = false,
)

@Singleton
class DeviceModelService {
    private val store = ConcurrentHashMap<Long, ConcurrentHashMap<String, DeviceVariable>>()

    fun seedDefaults(
        chargePointId: Long,
        vendor: String,
        model: String,
        serial: String,
        firmware: String,
    ) {
        val map = store.getOrPut(chargePointId) { ConcurrentHashMap() }
        fun put(
            variable: DeviceVariable,
        ) {
            map[key(variable)] = variable
        }
        put(DeviceVariable("ChargingStation", null, "VendorName", null, vendor, true))
        put(DeviceVariable("ChargingStation", null, "Model", null, model, true))
        put(DeviceVariable("ChargingStation", null, "SerialNumber", null, serial, true))
        put(DeviceVariable("ChargingStation", null, "FirmwareVersion", null, firmware, true))
        put(DeviceVariable("OCPPCommCtrlr", null, "HeartbeatInterval", null, "60", false))
        put(DeviceVariable("AuthCtrlr", null, "LocalPreAuthorize", null, "false", false))
    }

    fun getAll(
        chargePointId: Long,
    ): List<DeviceVariable> = store[chargePointId]?.values?.toList().orEmpty()

    fun get(
        chargePointId: Long,
        componentName: String,
        variableName: String,
        componentInstance: String? = null,
        variableInstance: String? = null,
    ): DeviceVariable? {
        return store[chargePointId]?.get(
            key(componentName, componentInstance, variableName, variableInstance),
        )
    }

    fun set(
        chargePointId: Long,
        variable: DeviceVariable,
    ): Boolean {
        val map = store.getOrPut(chargePointId) { ConcurrentHashMap() }
        val existing = map[key(variable)]
        if (existing?.readonly == true) {
            return false
        }
        map[key(variable)] = variable
        return true
    }

    private fun key(
        variable: DeviceVariable,
    ): String {
        return key(
            variable.componentName,
            variable.componentInstance,
            variable.variableName,
            variable.variableInstance,
        )
    }

    private fun key(
        componentName: String,
        componentInstance: String?,
        variableName: String,
        variableInstance: String?,
    ): String = listOf(
        componentName,
        componentInstance.orEmpty(),
        variableName,
        variableInstance.orEmpty(),
    ).joinToString("|")
}
