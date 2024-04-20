package com.monta.ocpp.emulator.v16.service.ocpp.util

import com.monta.library.core.util.DateUtil
import com.monta.library.ocpp.v16.Location
import com.monta.library.ocpp.v16.SampledValue
import com.monta.library.ocpp.v16.ValueFormat
import java.time.Duration
import java.time.Instant
import kotlin.math.min

object MeterValuesGenerator {
    fun generate(
        meterValuesSampledData: List<String>,
        startTime: Instant?,
        endMeter: Double,
        watts: Double,
        numberPhases: Int = 3
    ): List<SampledValue> {
        val ampsPerPhase = (watts / numberPhases) / 230.0
        val sampledValues = mutableListOf<SampledValue>()

        if (meterValuesSampledData.contains("Energy.Active.Import.Register")) {
            sampledValues.add(
                SampledValue(
                    value = endMeter.toString(),
                    context = "Sample.Periodic",
                    format = ValueFormat.Raw.name,
                    measurand = "Energy.Active.Import.Register",
                    location = Location.Outlet.name,
                    unit = "Wh"
                )
            )
        }

        if (meterValuesSampledData.contains("Current.Import")) {
            sampledValues.addAll(
                listOf(1, 2, 3, null).map { phase ->
                    sampledAmps(ampsPerPhase, phase, numberPhases)
                }
            )
        }
        if (meterValuesSampledData.contains("Voltage")) {
            sampledValues.addAll(
                listOf("L1", "L2", "L3").map { phase ->
                    sampledVoltage(230, phase)
                }
            )
        }
        if (meterValuesSampledData.contains("Power.Active.Import")) {
            sampledValues.addAll(
                listOf(1, 2, 3, null).map { phase ->
                    sampledPower(ampsPerPhase * 230, phase, numberPhases)
                }
            )
        }
        // if meter values for a transaction and SoC measurand
        // calculate as 20% + 5% for each minute charging
        if (meterValuesSampledData.contains("SoC") && (startTime != null)) {
            sampledValues.add(
                SampledValue(
                    value = min(20 + (5 * Duration.between(startTime, DateUtil.getInstant()).toMinutes()), 100).toString(),
                    context = "Sample.Periodic",
                    format = ValueFormat.Raw.name,
                    measurand = "SoC",
                    location = Location.EV.name,
                    unit = "Percent"
                )
            )
        }

        return sampledValues
    }

    private fun sampledAmps(ampsPerPhase: Double, phase: Int? = null, numberPhases: Int) = SampledValue(
        value = (if (phase == null) (numberPhases * ampsPerPhase) else if (phase <= numberPhases) ampsPerPhase else 0).toString(),
        context = "Sample.Periodic",
        format = ValueFormat.Raw.name,
        measurand = "Current.Import",
        phase = phase?.let { "L$it" },
        unit = "A"
    )

    private fun sampledVoltage(voltage: Int, phase: String? = null) = SampledValue(
        value = voltage.toString(),
        context = "Sample.Periodic",
        format = ValueFormat.Raw.name,
        measurand = "Voltage",
        phase = phase,
        unit = "V"
    )

    private fun sampledPower(wattPerPhase: Double, phase: Int? = null, numberPhases: Int) = SampledValue(
        value = (if (phase == null) (numberPhases * wattPerPhase) else if (phase <= numberPhases) wattPerPhase else 0).toString(),
        context = "Sample.Periodic",
        format = ValueFormat.Raw.name,
        measurand = "Power.Active.Import",
        phase = phase?.let { "L$it" },
        unit = "W"
    )
}
