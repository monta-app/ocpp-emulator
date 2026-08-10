package com.monta.ocpp.emulator.ocpp.v21.extension

import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState
import com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common.ConnectorStatusEnum
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusMappingTest {
    @Test
    fun `maps 1_6 charging states to occupied`() {
        assertEquals(ConnectorStatusEnum.Occupied, ChargePointStatus.Charging.toOcpp21ConnectorStatus())
        assertEquals(ConnectorStatusEnum.Occupied, ChargePointStatus.Preparing.toOcpp21ConnectorStatus())
        assertEquals(ConnectorStatusEnum.Reserved, ChargePointStatus.Reserved.toOcpp21ConnectorStatus())
        assertEquals(ConnectorStatusEnum.Faulted, ChargePointStatus.Faulted.toOcpp21ConnectorStatus())
    }

    @Test
    fun `available becomes occupied when car is plugged`() {
        assertEquals(
            ConnectorStatusEnum.Occupied,
            ChargePointStatus.Available.toOcpp21ConnectorStatus(carState = CarState.B),
        )
        assertEquals(
            ConnectorStatusEnum.Available,
            ChargePointStatus.Available.toOcpp21ConnectorStatus(carState = CarState.A),
        )
    }
}
