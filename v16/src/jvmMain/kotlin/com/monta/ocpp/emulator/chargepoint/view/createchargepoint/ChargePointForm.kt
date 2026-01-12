package com.monta.ocpp.emulator.chargepoint.view.createchargepoint

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointTable
import com.monta.ocpp.emulator.chargepoint.model.OcppVersion
import com.monta.ocpp.emulator.chargepoint.service.ChargePointService
import com.monta.ocpp.emulator.common.components.FormInput
import com.monta.ocpp.emulator.common.components.LabelledCheckBox
import com.monta.ocpp.emulator.common.components.MontaIcon
import com.monta.ocpp.emulator.common.components.PasswordField
import com.monta.ocpp.emulator.common.components.Spinner
import com.monta.ocpp.emulator.common.components.getButtonStateColor
import com.monta.ocpp.emulator.common.components.getCardStyle
import com.monta.ocpp.emulator.common.model.UrlChoice
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.view.NavigationViewModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Factory

@Composable
fun ChargePointForm(
    viewModel: ChargePointFormViewModel,
    chargePoint: ChargePointDAO?,
) {
    chargePoint?.let {
        if (!viewModel.initialized) {
            viewModel.form.updateFromDAO(it)
            viewModel.form = viewModel.form.copy()
            viewModel.initialized = true
            viewModel.isUpdating = true
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(
                state = rememberScrollState(),
            ),
    ) {
        Card(
            modifier = getCardStyle()
                .align(Alignment.Center)
                .padding(
                    top = 32.dp,
                    bottom = 32.dp,
                ),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
                    .width(400.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(
                    text = (if (viewModel.isUpdating) "Edit" else "Create") + " Charge Point",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(8.dp)
                        .align(Alignment.Start)
                        .fillMaxWidth(),
                )
                FormInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Charge Point Name",
                    value = viewModel.form.chargePointName,
                    onValueChange = { newValue ->
                        viewModel.form = viewModel.form.copy(
                            chargePointName = newValue,
                        )
                    },
                    helperText = "Name used for identifying the charge point locally in the app, not largely important mostly for your convenience",
                )
                FormInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Charge Point Identity",
                    value = viewModel.form.chargePointIdentity,
                    onValueChange = { newValue ->
                        viewModel.form = viewModel.form.copy(
                            chargePointIdentity = newValue,
                        )
                        viewModel.validateForm()
                    },
                    enabled = !viewModel.isUpdating,
                    isError = viewModel.formErrors.contains("identity"),
                    helperText = viewModel.formErrors.getOrDefault("identity", null)
                        ?: "The identity used for connecting to the OCPP server, cannot be empty or contain spaces, and must be unique",
                )
                Spinner(
                    modifier = Modifier.fillMaxWidth(),
                    label = "OCPP Server",
                    value = viewModel.form.urlChoice,
                    values = UrlChoice.entries,
                    render = { urlChoice ->
                        urlChoice.name
                    },
                ) { newUrlChoice ->
                    viewModel.form = viewModel.form.copy(
                        urlChoice = newUrlChoice,
                        ocppUrl = newUrlChoice.ocppUrl,
                        apiUrl = newUrlChoice.apiUrl,
                    )
                }
                FormInput(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Ocpp Url",
                    value = viewModel.form.ocppUrl,
                    onValueChange = { newValue ->
                        viewModel.form = viewModel.form.copy(
                            ocppUrl = newValue,
                        )
                    },
                    enabled = viewModel.form.urlChoice == UrlChoice.Other,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Connectors",
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    MontaIcon(
                        iconName = "help",
                        tooltipText = "Allows you to select how many connectors this charge point will be configured with",
                    )
                }

                Row {
                    Button(
                        modifier = Modifier.fillMaxWidth()
                            .weight(1f)
                            .padding(end = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = getButtonStateColor(viewModel.form.connectorCount == 1),
                        ),
                        onClick = {
                            viewModel.form = viewModel.form.copy(
                                connectorCount = 1,
                            )
                        },
                    ) {
                        Text("1")
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth()
                            .weight(1f)
                            .padding(start = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = getButtonStateColor(viewModel.form.connectorCount == 2),
                        ),
                        onClick = {
                            viewModel.form = viewModel.form.copy(
                                connectorCount = 2,
                            )
                        },
                    ) {
                        Text("2")
                    }
                }

                LabelledCheckBox(
                    modifier = Modifier.fillMaxWidth(),
                    checked = viewModel.form.showAdvanceSettings,
                    onCheckedChange = {
                        viewModel.form = viewModel.form.copy(
                            showAdvanceSettings = it,
                        )
                    },
                    label = "Show Advanced Settings",
                )

                if (viewModel.form.showAdvanceSettings) {
                    FormInput(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Api Url",
                        value = viewModel.form.apiUrl,
                        onValueChange = { newValue ->
                            viewModel.form = viewModel.form.copy(
                                apiUrl = newValue,
                            )
                        },
                    )
                    PasswordField(
                        modifier = Modifier.fillMaxWidth(),
                        password = viewModel.form.password,
                        passwordVisibility = viewModel.form.passwordVisibility,
                        passwordListener = { newValue ->
                            viewModel.form = viewModel.form.copy(
                                password = newValue,
                            )
                        },
                        passwordVisibilityListener = { newValue ->
                            viewModel.form = viewModel.form.copy(
                                passwordVisibility = newValue,
                            )
                        },
                    )
                    Spinner(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Max charging rate",
                        value = viewModel.form.maxKw,
                        values = listOf(22, 150, 250, 350),
                        render = { "$it kW" },
                        onSelectionChanged = { newMaxKw ->
                            viewModel.form = viewModel.form.copy(
                                maxKw = newMaxKw.toDouble(),
                            )
                        },
                    )
                    Spinner(
                        modifier = Modifier.fillMaxWidth(),
                        label = "OCPP version",
                        value = viewModel.form.ocppVersion,
                        values = OcppVersion.entries,
                        render = { ocppVersionChoice ->
                            ocppVersionChoice.version
                        },
                    ) { newOcppChoice ->
                        viewModel.form = viewModel.form.copy(
                            ocppVersion = newOcppChoice,
                        )
                    }
                    FormInput(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Firmware Version",
                        value = viewModel.form.firmware,
                        onValueChange = { newValue ->
                            viewModel.form = viewModel.form.copy(
                                firmware = newValue,
                            )
                        },
                    )
                }
                Button(
                    enabled = viewModel.formErrors.isEmpty(),
                    modifier = Modifier.align(Alignment.Start),
                    onClick = {
                        connect(viewModel)
                    },
                ) {
                    if (viewModel.isUpdating) {
                        Text("Save")
                    } else {
                        Text("Create")
                    }
                }
            }
        }
    }
}

private fun connect(
    viewModel: ChargePointFormViewModel,
) {
    val navigationViewModel: NavigationViewModel by injectAnywhere()
    val chargePointService: ChargePointService by injectAnywhere()

    if (viewModel.validateForm()) {
        return
    }

    chargePointService.upsert(
        name = viewModel.form.chargePointName,
        identity = viewModel.form.chargePointIdentity,
        password = viewModel.form.password.takeIf { it.isNotBlank() },
        ocppUrl = viewModel.form.ocppUrl,
        apiUrl = viewModel.form.apiUrl,
        firmware = viewModel.form.firmware,
        maxKw = viewModel.form.maxKw,
        connectorCount = viewModel.form.connectorCount,
    )

    navigationViewModel.navigateTo(
        screen = NavigationViewModel.Screen.ChargePoints,
    )
}

@Factory
class ChargePointFormViewModel {

    var form by mutableStateOf(Form())
    var initialized by mutableStateOf(false)
    var isUpdating by mutableStateOf(false)

    val formErrors = mutableStateMapOf<String, String>()

    fun validateForm(): Boolean {
        var hasErrors = false

        if (form.ocppUrl.isBlank()) {
            formErrors["ocppUrl"] = "URL cannot be blank or empty"
            hasErrors = true
        } else if (!form.ocppUrl.isWebsocketUrl()) {
            formErrors["ocppUrl"] = "URL not a valid web socket url"
            hasErrors = true
        }

        if (form.chargePointIdentity.isBlank()) {
            formErrors["identity"] = "Cannot be blank or empty"
            hasErrors = true
        } else if (!isUpdating &&
            transaction {
                ChargePointDAO.count(
                    ChargePointTable.identity eq form.chargePointIdentity.trim().uppercase(),
                ) != 0L
            }
        ) {
            formErrors["identity"] = "Identity already in use"
            hasErrors = true
        } else if (form.urlChoice == UrlChoice.Production &&
            !form.chargePointIdentity.trim().uppercase()
                .startsWith("MEM_")
        ) {
            formErrors["identity"] = "On production identity must begin with MEM_"
            hasErrors = true
        } else {
            formErrors.remove("identity")
        }

        return hasErrors
    }

    data class Form(
        var chargePointName: String = "",
        var chargePointIdentity: String = "",
        var password: String = "",
        var passwordVisibility: Boolean = false,
        var urlChoice: UrlChoice = UrlChoice.Staging,
        var ocppUrl: String = UrlChoice.Staging.ocppUrl,
        var apiUrl: String = UrlChoice.Staging.apiUrl,
        var connectorCount: Int = 1,
        var showAdvanceSettings: Boolean = false,
        var firmware: String = "1.0.0",
        var maxKw: Double = 22.0,
        var ocppVersion: OcppVersion = OcppVersion.V16,
    ) {
        fun updateFromDAO(
            chargePoint: ChargePointDAO,
        ) {
            this.chargePointName = chargePoint.name
            this.chargePointIdentity = chargePoint.identity
            this.password = chargePoint.basicAuthPassword ?: this.password
            this.urlChoice = UrlChoice.fromUrl(chargePoint.ocppUrl)
            this.ocppUrl = chargePoint.ocppUrl
            this.apiUrl = chargePoint.apiUrl
            this.connectorCount = transaction {
                chargePoint.connectors.count().toInt()
            }
            this.firmware = chargePoint.firmware
            this.maxKw = chargePoint.maxKw
            this.ocppVersion = chargePoint.ocppVersion
        }
    }

    private fun String.isWebsocketUrl(): Boolean {
        return startsWith("wss://") || startsWith("ws://")
    }
}
