package com.monta.ocpp.emulator.v16.service.ocpp.profile

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.localauth.GetLocalListVersionConfirmation
import com.monta.library.ocpp.v16.localauth.GetLocalListVersionRequest
import com.monta.library.ocpp.v16.localauth.LocalListClientProfile
import com.monta.library.ocpp.v16.localauth.SendLocalListConfirmation
import com.monta.library.ocpp.v16.localauth.SendLocalListRequest
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.v16.data.service.ChargePointService
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton
class LocalAuthHandler : LocalListClientProfile.Listener {

    private val chargePointService: ChargePointService by injectAnywhere()

    override suspend fun getLocalListVersion(
        ocppSessionInfo: OcppSession.Info,
        request: GetLocalListVersionRequest
    ): GetLocalListVersionConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)
        return GetLocalListVersionConfirmation(
            listVersion = chargePoint.localAuthList.version
        )
    }

    override suspend fun sendLocalList(
        ocppSessionInfo: OcppSession.Info,
        request: SendLocalListRequest
    ): SendLocalListConfirmation {
        val chargePoint = chargePointService.getByIdentity(ocppSessionInfo.identity)

        transaction {
            chargePoint.localAuthList.version = request.listVersion

            when (request.updateType) {
                SendLocalListRequest.UpdateType.Differential -> {
                    request.localAuthorizationList?.forEach { token ->
                        if (token.idTagInfo == null) {
                            chargePoint.localAuthList.tokens.remove(token.idTag)
                        } else {
                            chargePoint.localAuthList.tokens.add(token.idTag)
                        }
                    }
                }

                SendLocalListRequest.UpdateType.Full -> {
                    chargePoint.localAuthList.tokens.clear()
                    request.localAuthorizationList?.let { tokenList ->
                        chargePoint.localAuthList.tokens.addAll(tokenList.map { it.idTag })
                    }
                }
            }
        }

        return SendLocalListConfirmation(
            status = SendLocalListConfirmation.Status.Accepted
        )
    }
}
