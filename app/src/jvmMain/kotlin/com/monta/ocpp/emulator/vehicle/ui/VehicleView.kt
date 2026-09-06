package com.monta.ocpp.emulator.vehicle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.designsystem.ui.component.FormInput
import com.monta.ocpp.emulator.designsystem.ui.component.InputField
import com.monta.ocpp.emulator.designsystem.ui.component.LabelledCheckBox
import com.monta.ocpp.emulator.designsystem.ui.component.PasswordField
import com.monta.ocpp.emulator.designsystem.ui.component.PrimaryButton
import com.monta.ocpp.emulator.designsystem.ui.component.SectionCard
import com.monta.ocpp.emulator.designsystem.ui.component.Select
import com.monta.ocpp.emulator.platform.config.model.UrlChoice
import com.monta.ocpp.emulator.platform.util.PrettyYamlFormatter
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import com.monta.ocpp.emulator.vehicle.service.VehicleService
import kotlinx.coroutines.runBlocking

@Composable
fun VehicleView() {
    val vehicleService: VehicleService by injectAnywhere()

    var urlChoice by remember {
        mutableStateOf(
            UrlChoice.fromVehicleServiceUrl(
                vehicleService.getVehicleServiceUrl(),
            ),
        )
    }

    var vehicleServiceUrl by remember {
        mutableStateOf(vehicleService.getVehicleServiceUrl())
    }

    var enodeSecretVisibility: Boolean by remember {
        mutableStateOf(false)
    }

    var integrationExternalId by remember {
        mutableStateOf(vehicleService.getVehicleIntegrationExternalId())
    }

    var enodeSecretKey by remember {
        mutableStateOf(vehicleService.getEnodeSecretKey())
    }

    var externalVehicleId by remember {
        mutableStateOf(vehicleService.getVehicleExternalId())
    }

    var soc by remember {
        mutableStateOf(50.0)
    }

    var advancedMode by remember {
        mutableStateOf(false)
    }

    var vehiclePayload by remember {
        mutableStateOf(defaultVehicle(externalVehicleId, soc))
    }

    var vehicleYaml by remember {
        mutableStateOf("")
    }

    SectionCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.width(400.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Select(
                modifier = Modifier.fillMaxWidth(),
                label = "Server",
                value = urlChoice,
                values = UrlChoice.entries,
                render = { urlChoice ->
                    urlChoice.name
                },
            ) { newUrlChoice ->
                urlChoice = newUrlChoice
                vehicleServiceUrl = newUrlChoice.vehicleServiceUrl
            }
            FormInput(
                modifier = Modifier.fillMaxWidth(),
                label = "Base Url",
                value = vehicleServiceUrl,
                onValueChange = { newValue ->
                    vehicleServiceUrl = newValue
                },
                enabled = urlChoice == UrlChoice.Other,
            )
            PasswordField(
                modifier = Modifier.fillMaxWidth(),
                label = "Enode secret key",
                password = enodeSecretKey,
                passwordVisibility = enodeSecretVisibility,
                passwordListener = { newValue -> enodeSecretKey = newValue },
                passwordVisibilityListener = { newValue ->
                    enodeSecretVisibility = newValue
                },
            )
            InputField(
                value = integrationExternalId,
                onValueChange = { newValue -> integrationExternalId = newValue },
                modifier = Modifier.fillMaxWidth(),
                label = "Vehicle integration external id",
            )

            if (!advancedMode) {
                InputField(
                    value = externalVehicleId,
                    onValueChange = { newValue -> externalVehicleId = newValue },
                    modifier = Modifier.fillMaxWidth(),
                    label = "External vehicle id",
                )
                InputField(
                    value = soc.toString(),
                    onValueChange = { newValue: String -> soc = newValue.toDouble() },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Battery percentage",
                )
            }
            LabelledCheckBox(
                modifier = Modifier.fillMaxWidth(),
                checked = advancedMode,
                onCheckedChange = {
                    advancedMode = it
                    if (it) {
                        vehicleYaml = PrettyYamlFormatter.writeYaml(
                            vehiclePayload.withValues(
                                externalVehicleId = externalVehicleId,
                                soc = soc,
                            ),
                        )
                    } else {
                        vehiclePayload = parseVehicleCatching(vehicleYaml, vehiclePayload)
                        externalVehicleId = vehiclePayload.id
                        soc = vehiclePayload.chargeState?.batteryLevel?.toDouble() ?: 50.0
                    }
                },
                label = "Advanced mode",
            )
            if (advancedMode) {
                InputField(
                    value = vehicleYaml,
                    onValueChange = { newValue -> vehicleYaml = newValue },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Vehicle Payload",
                    singleLine = false,
                    minHeight = 120.dp,
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                )
            }
            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    vehiclePayload = if (advancedMode) {
                        parseVehicleCatching(vehicleYaml, vehiclePayload)
                    } else {
                        vehiclePayload.withValues(externalVehicleId, soc)
                    }
                    vehicleService.store(
                        vehicleIntegrationExternalId = integrationExternalId,
                        enodeSecretKey = enodeSecretKey,
                        vehicleExternalId = externalVehicleId,
                        vehicleServiceUrl = vehicleServiceUrl,
                    )
                    runBlocking {
                        vehicleService.sendUpdate(
                            integrationExternalId = integrationExternalId,
                            vehicle = vehiclePayload,
                            host = vehicleServiceUrl,
                            enodeSecretKey = enodeSecretKey,
                        )
                    }
                },
            ) {
                Text("Send Vehicle Update")
            }
        }
    }
}
