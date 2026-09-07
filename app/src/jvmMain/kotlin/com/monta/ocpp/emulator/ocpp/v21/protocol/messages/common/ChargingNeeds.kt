// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import java.time.ZonedDateTime

data class ChargingNeeds(
    val requestedEnergyTransfer: EnergyTransferModeEnum,
    val acChargingParameters: ACChargingParameters? = null,
    val derChargingParameters: DERChargingParameters? = null,
    val evEnergyOffer: EVEnergyOffer? = null,
    val dcChargingParameters: DCChargingParameters? = null,
    val v2xChargingParameters: V2XChargingParameters? = null,
    /** *(2.1)* Modes of energy transfer that are marked as available by EV. */
    val availableEnergyTransfer: List<EnergyTransferModeEnum>? = null,
    val controlMode: ControlModeEnum? = null,
    val mobilityNeedsMode: MobilityNeedsModeEnum? = null,
    /** Estimated departure time of the EV. + *ISO 15118-2:* AC/DC_EVChargeParameterType: DepartureTime + *ISO 15118-20:* Dynamic/Scheduled_SEReqControlModeType: DepartureTIme */
    val departureTime: ZonedDateTime? = null,
    val customData: CustomData? = null,
)
