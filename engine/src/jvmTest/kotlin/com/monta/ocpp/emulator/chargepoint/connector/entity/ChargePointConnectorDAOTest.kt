package com.monta.ocpp.emulator.chargepoint.connector.entity

import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.connector.model.CarState
import com.monta.ocpp.emulator.chargepoint.transaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.testsupport.ChargePointFixtures
import com.monta.ocpp.emulator.testsupport.DatabaseSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ChargePointConnectorDAOTest : DatabaseSpec({

    fun connector(
        maxKw: Double = 22.0,
    ): ChargePointConnectorDAO {
        val chargePoint = ChargePointFixtures.newChargePoint(maxKw = maxKw)
        return ChargePointFixtures.connectorOf(chargePoint)
    }

    describe("calculateState") {

        it("reports Available when no car is plugged in") {
            val connector = connector()
            ChargePointFixtures.setConnectorState(connector) { carState = CarState.A }

            transaction { connector.calculateState() } shouldBe ChargePointStatus.Available
        }

        it("reports Available for an unplugged car even after a charge just stopped") {
            val connector = connector()
            ChargePointFixtures.setConnectorState(connector) { carState = CarState.A }

            transaction { connector.calculateState(justStopped = true) } shouldBe ChargePointStatus.Available
        }

        it("reports Preparing for a plugged-in car with no transaction") {
            val connector = connector()
            ChargePointFixtures.setConnectorState(connector) { carState = CarState.B }

            transaction { connector.calculateState() } shouldBe ChargePointStatus.Preparing
        }

        it("reports Finishing for a plugged-in car whose charge just stopped") {
            val connector = connector()
            ChargePointFixtures.setConnectorState(connector) { carState = CarState.B }

            transaction { connector.calculateState(justStopped = true) } shouldBe ChargePointStatus.Finishing
        }

        it("reports Charging for a car drawing current under an active transaction") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)
            ChargePointFixtures.startTransaction(chargePoint, connector)
            ChargePointFixtures.setConnectorState(connector) {
                carState = CarState.C
                kw = 7.4
            }

            transaction { connector.calculateState() } shouldBe ChargePointStatus.Charging
        }

        it("reports SuspendedEV for a plugged-in car that is not drawing under an active transaction") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)
            ChargePointFixtures.startTransaction(chargePoint, connector)
            ChargePointFixtures.setConnectorState(connector) {
                carState = CarState.B
                kw = 7.4
            }

            transaction { connector.calculateState() } shouldBe ChargePointStatus.SuspendedEV
        }

        it("reports SuspendedEVSE when the charger itself is withholding power") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)
            ChargePointFixtures.startTransaction(chargePoint, connector)
            ChargePointFixtures.setConnectorState(connector) {
                carState = CarState.C
                kw = 0.0
            }

            transaction { connector.calculateState() } shouldBe ChargePointStatus.SuspendedEVSE
        }

        it("prefers SuspendedEVSE over SuspendedEV when the car is idle and the charger is at zero") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)
            ChargePointFixtures.startTransaction(chargePoint, connector)
            ChargePointFixtures.setConnectorState(connector) {
                carState = CarState.B
                kw = 0.0
            }

            transaction { connector.calculateState() } shouldBe ChargePointStatus.SuspendedEVSE
        }

        it("reports Preparing for a charging-ready car once its transaction is cleared") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)
            ChargePointFixtures.startTransaction(chargePoint, connector)
            ChargePointFixtures.setConnectorState(connector) {
                carState = CarState.C
                activeTransaction = null
            }

            transaction { connector.calculateState() } shouldBe ChargePointStatus.Preparing
        }
    }

    describe("updateKw") {

        it("draws the full rating when neither the vehicle nor a profile limits it") {
            val connector = connector(maxKw = 22.0)

            ChargePointFixtures.setConnectorState(connector) { updateKw() }

            transaction { connector.kw } shouldBe (22.0 plusOrMinus 0.001)
        }

        it("is capped by what the vehicle will accept per phase") {
            val connector = connector(maxKw = 22.0)

            ChargePointFixtures.setConnectorState(connector) {
                vehicleMaxAmpsPerPhase = 10.0
                updateKw()
            }

            transaction { connector.kw } shouldBe (6.9 plusOrMinus 0.001)
        }

        it("is capped by a smart-charging profile below the connector rating") {
            val connector = connector(maxKw = 22.0)

            ChargePointFixtures.setConnectorState(connector) { updateKw(chargingProfileWatts = 6900.0) }

            transaction { connector.kw } shouldBe (6.9 plusOrMinus 0.001)
        }

        it("ignores a profile that asks for more than the connector is rated for") {
            val connector = connector(maxKw = 22.0)

            ChargePointFixtures.setConnectorState(connector) { updateKw(chargingProfileWatts = 100_000.0) }

            transaction { connector.kw } shouldBe (22.0 plusOrMinus 0.001)
        }

        it("scales down with the number of phases the vehicle is using") {
            val connector = connector(maxKw = 22.0)

            ChargePointFixtures.setConnectorState(connector) {
                vehicleMaxAmpsPerPhase = 10.0
                vehicleNumberPhases = 1
                updateKw()
            }

            transaction { connector.kw } shouldBe (2.3 plusOrMinus 0.001)
        }

        it("drops to zero when a profile asks for no power at all") {
            val connector = connector(maxKw = 22.0)

            ChargePointFixtures.setConnectorState(connector) { updateKw(chargingProfileWatts = 0.0) }

            transaction { connector.kw } shouldBe 0.0
        }
    }

    describe("derived readings") {

        it("converts the connector rating into watt-hours accrued per second") {
            val connector = connector(maxKw = 22.0)

            ChargePointFixtures.setConnectorState(connector) { kw = 7.2 }

            transaction { connector.wattHoursPerSecond } shouldBe (2.0 plusOrMinus 0.001)
        }

        it("sums the meter across every transaction the connector has run") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)

            transaction {
                ChargePointTransactionDAO.newInstance(
                    chargePoint = chargePoint,
                    chargePointConnector = connector,
                    externalId = 1,
                    idTag = "TAG",
                    endMeter = 1500.0,
                )
                ChargePointTransactionDAO.newInstance(
                    chargePoint = chargePoint,
                    chargePointConnector = connector,
                    externalId = 2,
                    idTag = "TAG",
                    endMeter = 2500.0,
                )
            }

            transaction { ChargePointConnectorDAO.findById(connector.id)?.meterWh } shouldBe
                (4000.0 plusOrMinus 0.001)
        }
    }
})
