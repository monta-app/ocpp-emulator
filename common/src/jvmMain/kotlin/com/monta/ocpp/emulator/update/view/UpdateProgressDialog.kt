package com.monta.ocpp.emulator.update.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.update.model.GithubRelease

@Composable
internal fun UpdateProgressDialog(
    latestRelease: GithubRelease,
    downloadProgress: Float
) {
    AlertDialog(
        modifier = Modifier.width(400.dp),
        title = {
            Text(
                text = "Downloading - ${latestRelease.tagName}",
                style = MaterialTheme.typography.h6
            )
        },
        onDismissRequest = {},
        confirmButton = {},
        dismissButton = {},
        text = {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = downloadProgress
            )
        }
    )
}
