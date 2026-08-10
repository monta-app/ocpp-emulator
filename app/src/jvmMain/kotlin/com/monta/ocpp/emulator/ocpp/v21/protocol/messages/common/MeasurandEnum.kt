// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

import com.fasterxml.jackson.annotation.JsonProperty

enum class MeasurandEnum {
    @JsonProperty("Current.Export")
    CurrentExport,

    @JsonProperty("Current.Export.Offered")
    CurrentExportOffered,

    @JsonProperty("Current.Export.Minimum")
    CurrentExportMinimum,

    @JsonProperty("Current.Import")
    CurrentImport,

    @JsonProperty("Current.Import.Offered")
    CurrentImportOffered,

    @JsonProperty("Current.Import.Minimum")
    CurrentImportMinimum,

    @JsonProperty("Current.Offered")
    CurrentOffered,

    @JsonProperty("Display.PresentSOC")
    DisplayPresentSOC,

    @JsonProperty("Display.MinimumSOC")
    DisplayMinimumSOC,

    @JsonProperty("Display.TargetSOC")
    DisplayTargetSOC,

    @JsonProperty("Display.MaximumSOC")
    DisplayMaximumSOC,

    @JsonProperty("Display.RemainingTimeToMinimumSOC")
    DisplayRemainingTimeToMinimumSOC,

    @JsonProperty("Display.RemainingTimeToTargetSOC")
    DisplayRemainingTimeToTargetSOC,

    @JsonProperty("Display.RemainingTimeToMaximumSOC")
    DisplayRemainingTimeToMaximumSOC,

    @JsonProperty("Display.ChargingComplete")
    DisplayChargingComplete,

    @JsonProperty("Display.BatteryEnergyCapacity")
    DisplayBatteryEnergyCapacity,

    @JsonProperty("Display.InletHot")
    DisplayInletHot,

    @JsonProperty("Energy.Active.Export.Interval")
    EnergyActiveExportInterval,

    @JsonProperty("Energy.Active.Export.Register")
    EnergyActiveExportRegister,

    @JsonProperty("Energy.Active.Import.Interval")
    EnergyActiveImportInterval,

    @JsonProperty("Energy.Active.Import.Register")
    EnergyActiveImportRegister,

    @JsonProperty("Energy.Active.Import.CableLoss")
    EnergyActiveImportCableLoss,

    @JsonProperty("Energy.Active.Import.LocalGeneration.Register")
    EnergyActiveImportLocalGenerationRegister,

    @JsonProperty("Energy.Active.Net")
    EnergyActiveNet,

    @JsonProperty("Energy.Active.Setpoint.Interval")
    EnergyActiveSetpointInterval,

    @JsonProperty("Energy.Apparent.Export")
    EnergyApparentExport,

    @JsonProperty("Energy.Apparent.Import")
    EnergyApparentImport,

    @JsonProperty("Energy.Apparent.Net")
    EnergyApparentNet,

    @JsonProperty("Energy.Reactive.Export.Interval")
    EnergyReactiveExportInterval,

    @JsonProperty("Energy.Reactive.Export.Register")
    EnergyReactiveExportRegister,

    @JsonProperty("Energy.Reactive.Import.Interval")
    EnergyReactiveImportInterval,

    @JsonProperty("Energy.Reactive.Import.Register")
    EnergyReactiveImportRegister,

    @JsonProperty("Energy.Reactive.Net")
    EnergyReactiveNet,

    @JsonProperty("EnergyRequest.Target")
    EnergyRequestTarget,

    @JsonProperty("EnergyRequest.Minimum")
    EnergyRequestMinimum,

    @JsonProperty("EnergyRequest.Maximum")
    EnergyRequestMaximum,

    @JsonProperty("EnergyRequest.Minimum.V2X")
    EnergyRequestMinimumV2X,

    @JsonProperty("EnergyRequest.Maximum.V2X")
    EnergyRequestMaximumV2X,

    @JsonProperty("EnergyRequest.Bulk")
    EnergyRequestBulk,
    Frequency,

    @JsonProperty("Power.Active.Export")
    PowerActiveExport,

    @JsonProperty("Power.Active.Import")
    PowerActiveImport,

    @JsonProperty("Power.Active.Setpoint")
    PowerActiveSetpoint,

    @JsonProperty("Power.Active.Residual")
    PowerActiveResidual,

    @JsonProperty("Power.Export.Minimum")
    PowerExportMinimum,

    @JsonProperty("Power.Export.Offered")
    PowerExportOffered,

    @JsonProperty("Power.Factor")
    PowerFactor,

    @JsonProperty("Power.Import.Offered")
    PowerImportOffered,

    @JsonProperty("Power.Import.Minimum")
    PowerImportMinimum,

    @JsonProperty("Power.Offered")
    PowerOffered,

    @JsonProperty("Power.Reactive.Export")
    PowerReactiveExport,

    @JsonProperty("Power.Reactive.Import")
    PowerReactiveImport,
    SoC,
    Voltage,

    @JsonProperty("Voltage.Minimum")
    VoltageMinimum,

    @JsonProperty("Voltage.Maximum")
    VoltageMaximum,
}
