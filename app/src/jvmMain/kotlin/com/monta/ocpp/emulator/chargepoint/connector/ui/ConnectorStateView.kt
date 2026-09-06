package com.monta.ocpp.emulator.chargepoint.connector.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.ocpp.emulator.chargepoint.connector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.designsystem.ui.component.AppDialog
import com.monta.ocpp.emulator.designsystem.ui.component.InputField
import com.monta.ocpp.emulator.designsystem.ui.component.OutlineButton
import com.monta.ocpp.emulator.designsystem.ui.component.PrimaryButton
import com.monta.ocpp.emulator.designsystem.ui.component.Select
import com.monta.ocpp.emulator.ocpp.v16.extension.setStatus
import com.monta.ocpp.emulator.platform.util.launchThread

@Composable
fun ColumnScope.ConnectorStateView(
    connector: ChargePointConnectorDAO,
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var connectorStatus by remember {
        mutableStateOf(connector.status)
    }

    var errorCode by remember {
        mutableStateOf(connector.errorCode)
    }

    var vendorId by remember {
        mutableStateOf(connector.vendorId)
    }

    var vendorErrorCode by remember {
        mutableStateOf(connector.vendorErrorCode)
    }

    var statusInfo by remember {
        mutableStateOf(connector.statusInfo)
    }

    OutlineButton(
        onClick = {
            expanded = true
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Connector Status")
    }

    if (expanded) {
        AppDialog(
            onDismissRequest = {
                expanded = false
            },
            title = "Connector Status",
            description = "Push a status notification for this connector to the CSMS.",
            modifier = Modifier.width(440.dp),
            confirmButton = {
                PrimaryButton(
                    onClick = {
                        launchThread {
                            connector.setStatus(
                                status = connectorStatus,
                                errorCode = errorCode,
                                vendorId = if (vendorId.isNullOrBlank()) null else vendorId,
                                vendorErrorCode = if (vendorErrorCode.isNullOrBlank()) null else vendorErrorCode,
                                info = if (statusInfo.isNullOrBlank()) null else statusInfo,
                                forceUpdate = true,
                            )
                        }
                        expanded = false
                    },
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                OutlineButton(
                    onClick = {
                        expanded = false
                    },
                ) {
                    Text("Close")
                }
            },
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Select(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Connector Status",
                    value = connectorStatus,
                    values = ChargePointStatus.entries,
                    render = { chargePointStatus ->
                        chargePointStatus.name
                    },
                    onSelectionChanged = { newChargePointStatus ->
                        connectorStatus = newChargePointStatus
                    },
                )
                Select(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Error Code",
                    value = errorCode,
                    values = ChargePointErrorCode.entries,
                    render = { chargePointErrorCode ->
                        chargePointErrorCode.name
                    },
                    onSelectionChanged = { newChargePointErrorCode ->
                        errorCode = newChargePointErrorCode
                    },
                )
                InputField(
                    value = statusInfo ?: "",
                    onValueChange = { newStatusInfo ->
                        statusInfo = newStatusInfo
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Info",
                )
                InputField(
                    value = vendorId ?: "",
                    onValueChange = { newVendorId ->
                        vendorId = newVendorId
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Vendor ID",
                )
                InputField(
                    value = vendorErrorCode ?: "",
                    onValueChange = { newVendorErrorCode ->
                        vendorErrorCode = newVendorErrorCode
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = "Vendor Error Code",
                )
            }
        }
    }
}
