package com.monta.ocpp.emulator.control.model

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode

data class ControlRequest(
    val command: String,
    val id: String? = null,
    val params: JsonNode = NullNode.instance,
)

data class ControlResponse(
    val id: String?,
    val ok: Boolean,
    val result: Any? = null,
    val error: ControlError? = null,
) {
    companion object {
        fun success(
            id: String?,
            result: Any? = null,
        ): ControlResponse {
            return ControlResponse(id = id, ok = true, result = result)
        }

        fun failure(
            id: String?,
            code: String,
            message: String,
        ): ControlResponse {
            return ControlResponse(id = id, ok = false, error = ControlError(code, message))
        }
    }
}

data class ControlError(
    val code: String,
    val message: String,
)
