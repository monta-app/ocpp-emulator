// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.tariff

import com.monta.library.ocpp.common.profile.OcppConfirmation
import com.monta.library.ocpp.common.profile.OcppRequest
import com.monta.library.ocpp.common.profile.ProfileDispatcher
import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.common.transport.OcppCallException
import com.monta.ocpp.emulator.ocpp.v21.protocol.error.MessageErrorCodeV21
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ChangeTransactionTariffFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ChangeTransactionTariffRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ChangeTransactionTariffResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearTariffsFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearTariffsRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.ClearTariffsResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetTariffsFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetTariffsRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.GetTariffsResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDefaultTariffFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDefaultTariffRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.SetDefaultTariffResponse
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.VatNumberValidationFeature
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.VatNumberValidationRequest
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.VatNumberValidationResponse

val tariffFeatures = listOf(
    SetDefaultTariffFeature,
    GetTariffsFeature,
    ClearTariffsFeature,
    ChangeTransactionTariffFeature,
    VatNumberValidationFeature,
)

class TariffClientDispatcher(
    private val listener: Listener,
) : ProfileDispatcher {
    override val featureList = tariffFeatures

    override suspend fun handleRequest(
        ocppSessionInfo: OcppSession.Info,
        request: OcppRequest,
    ): OcppConfirmation {
        return when (request) {
            is SetDefaultTariffRequest -> listener.setDefaultTariff(ocppSessionInfo, request)
            is GetTariffsRequest -> listener.getTariffs(ocppSessionInfo, request)
            is ClearTariffsRequest -> listener.clearTariffs(ocppSessionInfo, request)
            is ChangeTransactionTariffRequest -> listener.changeTransactionTariff(ocppSessionInfo, request)
            else -> throw OcppCallException(MessageErrorCodeV21.NotSupported, "Requested Action [${request.actionName()}] is recognized but not supported by the receiver")
        }
    }

    interface Listener {
        suspend fun setDefaultTariff(
            ocppSessionInfo: OcppSession.Info,
            request: SetDefaultTariffRequest,
        ): SetDefaultTariffResponse

        suspend fun getTariffs(
            ocppSessionInfo: OcppSession.Info,
            request: GetTariffsRequest,
        ): GetTariffsResponse

        suspend fun clearTariffs(
            ocppSessionInfo: OcppSession.Info,
            request: ClearTariffsRequest,
        ): ClearTariffsResponse

        suspend fun changeTransactionTariff(
            ocppSessionInfo: OcppSession.Info,
            request: ChangeTransactionTariffRequest,
        ): ChangeTransactionTariffResponse
    }

    interface Sender {
        suspend fun vatNumberValidation(
            request: VatNumberValidationRequest,
        ): VatNumberValidationResponse
    }
}
