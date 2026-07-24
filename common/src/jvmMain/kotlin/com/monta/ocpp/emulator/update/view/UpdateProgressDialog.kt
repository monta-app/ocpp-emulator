package com.monta.ocpp.emulator.update.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.common.components.AppDialog
import com.monta.ocpp.emulator.update.model.GithubRelease

@Composable
internal fun UpdateProgressDialog(
    latestRelease: GithubRelease,
    downloadProgress: Float,
) {
    AppDialog(
        onDismissRequest = {},
        title = "Downloading — ${latestRelease.tagName}",
        modifier = Modifier.width(400.dp),
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = downloadProgress,
        )
    }
}
