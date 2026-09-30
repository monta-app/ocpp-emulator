package com.monta.ocpp.emulator.chargepoint.txdefault.service

import com.monta.library.ocpp.common.chargingprofile.ChargingProfileKind
import com.monta.library.ocpp.common.chargingprofile.ChargingRateUnit
import com.monta.library.ocpp.v16.smartcharge.ChargingProfile
import com.monta.library.ocpp.v16.smartcharge.ChargingProfilePurposeType
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedule
import com.monta.library.ocpp.v16.smartcharge.ChargingSchedulePeriod
import com.monta.library.ocpp.v16.smartcharge.ClearChargingProfileRequest
import com.monta.ocpp.emulator.chargepoint.txdefault.entity.TxDefaultDAO
import com.monta.ocpp.emulator.chargepoint.txdefault.repository.TxDefaultRepository
import com.monta.ocpp.emulator.testsupport.ChargePointFixtures
import com.monta.ocpp.emulator.testsupport.DatabaseSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class TxDefaultServiceTest : DatabaseSpec({

    val service = TxDefaultService(TxDefaultRepository())

    fun txDefaultProfile(
        chargingProfileId: Int? = 1,
        stackLevel: Int = 0,
        limit: Double = 16.0,
        purpose: ChargingProfilePurposeType = ChargingProfilePurposeType.TxDefaultProfile,
    ): ChargingProfile {
        return ChargingProfile(
            chargingProfileId = chargingProfileId,
            stackLevel = stackLevel,
            chargingProfilePurpose = purpose,
            chargingProfileKind = ChargingProfileKind.Absolute,
            chargingSchedule = ChargingSchedule(
                chargingRateUnit = ChargingRateUnit.A,
                chargingSchedulePeriod = listOf(
                    ChargingSchedulePeriod(startPeriod = 0, limit = limit, numberPhases = 3),
                ),
            ),
        )
    }

    describe("store") {

        it("persists a TxDefault profile for a connector") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)

            service.store(chargePoint, connector, txDefaultProfile(chargingProfileId = 7))

            transaction {
                val stored = TxDefaultDAO.all().single()

                stored.chargingProfileId shouldBe 7
                stored.txDefaultProfile.chargingSchedule?.chargingSchedulePeriod?.single()?.limit shouldBe 16.0
            }
        }

        it("replaces a profile that is already stored under the same id") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)

            service.store(chargePoint, connector, txDefaultProfile(chargingProfileId = 7, limit = 16.0))
            service.store(chargePoint, connector, txDefaultProfile(chargingProfileId = 7, limit = 32.0))

            transaction {
                val stored = TxDefaultDAO.all().single()

                stored.txDefaultProfile.chargingSchedule?.chargingSchedulePeriod?.single()?.limit shouldBe 32.0
            }
        }

        it("keeps profiles with different ids side by side") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)

            service.store(chargePoint, connector, txDefaultProfile(chargingProfileId = 1))
            service.store(chargePoint, connector, txDefaultProfile(chargingProfileId = 2))

            transaction {
                TxDefaultDAO.all().map { stored -> stored.chargingProfileId } shouldContainExactlyInAnyOrder
                    listOf(1, 2)
            }
        }

        it("keeps the same profile id apart per connector") {
            val chargePoint = ChargePointFixtures.newChargePoint(connectorCount = 2)
            val first = ChargePointFixtures.connectorOf(chargePoint, position = 1)
            val second = ChargePointFixtures.connectorOf(chargePoint, position = 2)

            service.store(chargePoint, first, txDefaultProfile(chargingProfileId = 1))
            service.store(chargePoint, second, txDefaultProfile(chargingProfileId = 1))

            transaction { TxDefaultDAO.all().count() } shouldBe 2
        }

        it("rejects a profile that is not a TxDefaultProfile") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)

            shouldThrow<IllegalArgumentException> {
                service.store(
                    chargePoint,
                    connector,
                    txDefaultProfile(purpose = ChargingProfilePurposeType.TxProfile),
                )
            }
        }

        it("rejects a profile with no id, which OCPP 1.6 requires") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)

            shouldThrow<IllegalArgumentException> {
                service.store(chargePoint, connector, txDefaultProfile(chargingProfileId = null))
            }
        }
    }

    describe("clear") {

        it("clears the single profile named by id, whichever connector holds it") {
            val chargePoint = ChargePointFixtures.newChargePoint(connectorCount = 2)
            val first = ChargePointFixtures.connectorOf(chargePoint, position = 1)
            val second = ChargePointFixtures.connectorOf(chargePoint, position = 2)
            service.store(chargePoint, first, txDefaultProfile(chargingProfileId = 1))
            service.store(chargePoint, second, txDefaultProfile(chargingProfileId = 2))

            service.clear(chargePoint, null, ClearChargingProfileRequest(id = 2))

            transaction {
                TxDefaultDAO.all().map { stored -> stored.chargingProfileId } shouldContainExactlyInAnyOrder listOf(1)
            }
        }

        it("clears only the named connector when no id is given") {
            val chargePoint = ChargePointFixtures.newChargePoint(connectorCount = 2)
            val first = ChargePointFixtures.connectorOf(chargePoint, position = 1)
            val second = ChargePointFixtures.connectorOf(chargePoint, position = 2)
            service.store(chargePoint, first, txDefaultProfile(chargingProfileId = 1))
            service.store(chargePoint, second, txDefaultProfile(chargingProfileId = 2))

            service.clear(chargePoint, second, ClearChargingProfileRequest())

            transaction {
                TxDefaultDAO.all().map { stored -> stored.chargingProfileId } shouldContainExactlyInAnyOrder listOf(1)
            }
        }

        it("clears the whole charge point when neither id nor connector is given") {
            val chargePoint = ChargePointFixtures.newChargePoint(connectorCount = 2)
            val first = ChargePointFixtures.connectorOf(chargePoint, position = 1)
            val second = ChargePointFixtures.connectorOf(chargePoint, position = 2)
            service.store(chargePoint, first, txDefaultProfile(chargingProfileId = 1))
            service.store(chargePoint, second, txDefaultProfile(chargingProfileId = 2))

            service.clear(chargePoint, null, ClearChargingProfileRequest())

            transaction { TxDefaultDAO.all().count() } shouldBe 0
        }

        it("leaves another charge point's profiles alone") {
            val mine = ChargePointFixtures.newChargePoint(identity = "MEM_001")
            val theirs = ChargePointFixtures.newChargePoint(identity = "MEM_002")
            service.store(mine, ChargePointFixtures.connectorOf(mine), txDefaultProfile(chargingProfileId = 1))
            service.store(theirs, ChargePointFixtures.connectorOf(theirs), txDefaultProfile(chargingProfileId = 1))

            service.clear(mine, null, ClearChargingProfileRequest())

            transaction { TxDefaultDAO.all().count() } shouldBe 1
        }

        it("does nothing when the CSMS is clearing a purpose this table does not hold") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)
            service.store(chargePoint, connector, txDefaultProfile(chargingProfileId = 1))

            service.clear(
                chargePoint,
                null,
                ClearChargingProfileRequest(chargingProfilePurpose = ChargingProfilePurposeType.TxProfile),
            )

            transaction { TxDefaultDAO.all().count() } shouldBe 1
        }

        it("clears when the CSMS explicitly names the TxDefaultProfile purpose") {
            val chargePoint = ChargePointFixtures.newChargePoint()
            val connector = ChargePointFixtures.connectorOf(chargePoint)
            service.store(chargePoint, connector, txDefaultProfile(chargingProfileId = 1))

            service.clear(
                chargePoint,
                null,
                ClearChargingProfileRequest(chargingProfilePurpose = ChargingProfilePurposeType.TxDefaultProfile),
            )

            transaction { TxDefaultDAO.all().count() } shouldBe 0
        }
    }
})
