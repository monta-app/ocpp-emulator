// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.client

import com.monta.library.ocpp.client.BaseOcppClientBuilder
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.afrr.AfrrClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.authorization.AuthorizationClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.availability.AvailabilityClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.batteryswap.BatterySwapClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.certificatemanagement.CertificateManagementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.datatransfer.DataTransferClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.der.DerClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.diagnostics.DiagnosticsClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.displaymessage.DisplayMessageClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.dynamicschedule.DynamicScheduleClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.firmwaremanagement.FirmwareManagementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.localauthorizationlistmanagement.LocalAuthorizationListManagementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.metervalues.MeterValuesClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.periodiceventstream.PeriodicEventStreamClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.prioritycharging.PriorityChargingClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.provisioning.ProvisioningClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.remotecontrol.RemoteControlClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.reservation.ReservationClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.security.SecurityClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.settlement.SettlementClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.smartcharging.SmartChargingClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.tariff.TariffClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.tariffandcost.TariffAndCostClientDispatcher
import com.monta.ocpp.emulator.ocpp.v21.protocol.blocks.transactions.TransactionsClientDispatcher

class OcppClientV21Builder : BaseOcppClientBuilder<OcppClientV21Builder>() {
    fun addAuthorization(): OcppClientV21Builder {
        profiles.add(AuthorizationClientDispatcher())
        return this
    }

    fun addAvailability(
        listener: AvailabilityClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(AvailabilityClientDispatcher(listener))
        return this
    }

    fun addCertificateManagement(
        listener: CertificateManagementClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(CertificateManagementClientDispatcher(listener))
        return this
    }

    fun addDataTransfer(
        listener: DataTransferClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(DataTransferClientDispatcher(listener))
        return this
    }

    fun addDiagnostics(
        listener: DiagnosticsClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(DiagnosticsClientDispatcher(listener))
        return this
    }

    fun addDisplayMessage(
        listener: DisplayMessageClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(DisplayMessageClientDispatcher(listener))
        return this
    }

    fun addFirmwareManagement(
        listener: FirmwareManagementClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(FirmwareManagementClientDispatcher(listener))
        return this
    }

    fun addLocalAuthorizationListManagement(
        listener: LocalAuthorizationListManagementClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(LocalAuthorizationListManagementClientDispatcher(listener))
        return this
    }

    fun addMeterValues(): OcppClientV21Builder {
        profiles.add(MeterValuesClientDispatcher())
        return this
    }

    fun addProvisioning(
        listener: ProvisioningClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(ProvisioningClientDispatcher(listener))
        return this
    }

    fun addRemoteControl(
        listener: RemoteControlClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(RemoteControlClientDispatcher(listener))
        return this
    }

    fun addReservation(
        listener: ReservationClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(ReservationClientDispatcher(listener))
        return this
    }

    fun addSecurity(): OcppClientV21Builder {
        profiles.add(SecurityClientDispatcher())
        return this
    }

    fun addSmartCharging(
        listener: SmartChargingClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(SmartChargingClientDispatcher(listener))
        return this
    }

    fun addTariffAndCost(
        listener: TariffAndCostClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(TariffAndCostClientDispatcher(listener))
        return this
    }

    fun addTransactions(
        listener: TransactionsClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(TransactionsClientDispatcher(listener))
        return this
    }

    fun addTariff(
        listener: TariffClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(TariffClientDispatcher(listener))
        return this
    }

    fun addDer(
        listener: DerClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(DerClientDispatcher(listener))
        return this
    }

    fun addPeriodicEventStream(
        listener: PeriodicEventStreamClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(PeriodicEventStreamClientDispatcher(listener))
        return this
    }

    fun addPriorityCharging(
        listener: PriorityChargingClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(PriorityChargingClientDispatcher(listener))
        return this
    }

    fun addDynamicSchedule(
        listener: DynamicScheduleClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(DynamicScheduleClientDispatcher(listener))
        return this
    }

    fun addBatterySwap(
        listener: BatterySwapClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(BatterySwapClientDispatcher(listener))
        return this
    }

    fun addSettlement(): OcppClientV21Builder {
        profiles.add(SettlementClientDispatcher())
        return this
    }

    fun addAfrr(
        listener: AfrrClientDispatcher.Listener,
    ): OcppClientV21Builder {
        profiles.add(AfrrClientDispatcher(listener))
        return this
    }

    fun build(): OcppClientV21 {
        return OcppClientV21(
            onConnect = requireNotNull(onConnect),
            onDisconnect = requireNotNull(onDisconnect),
            ocppSessionRepository = requireNotNull(ocppSessionRepository),
            settings = requireNotNull(settings),
            profiles = profiles,
            sendHook = sendHook,
        )
    }
}
