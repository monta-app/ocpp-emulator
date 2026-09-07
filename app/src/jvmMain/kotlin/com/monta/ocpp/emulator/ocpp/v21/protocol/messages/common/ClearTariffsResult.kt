// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class ClearTariffsResult(
    val status: TariffClearStatusEnum,
    val statusInfo: StatusInfo? = null,
    /** Id of tariff for which _status_ is reported. If no tariffs were found, then this field is absent, and _status_ will be `NoTariff`. */
    val tariffId: String? = null,
    val customData: CustomData? = null,
)
