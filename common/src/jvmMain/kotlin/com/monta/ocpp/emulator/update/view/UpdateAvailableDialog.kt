package com.monta.ocpp.emulator.update.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.update.AppUpdateService
import com.monta.ocpp.emulator.update.model.GithubRelease

@Composable
internal fun UpdateAvailableDialog(
    latestRelease: GithubRelease
) {
    val appUpdateService: AppUpdateService by injectAnywhere()

    AlertDialog(
        modifier = Modifier.width(400.dp),
        title = {
            Text(
                text = "New Release - ${latestRelease.tagName}",
                style = MaterialTheme.typography.h6
            )
        },
        onDismissRequest = {},
        confirmButton = {
            Button(
                onClick = {
                    launchThread {
                        latestRelease.let { latestRelease ->
                            appUpdateService.update(latestRelease)
                        }
                    }
                }
            ) {
                Text("Download")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    appUpdateService.clearUpdate()
                }
            ) {
                Text("Close")
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                latestRelease.body.split("\n").forEach { block ->
                    if (block.startsWith("###")) {
                        Spacer(
                            modifier = Modifier.padding(
                                top = 8.dp
                            )
                        )
                        Text(
                            text = block.replace("### ", ""),
                            style = MaterialTheme.typography.subtitle2
                        )
                    } else {
                        Text(block)
                    }
                }
            }
        }
    )
}
