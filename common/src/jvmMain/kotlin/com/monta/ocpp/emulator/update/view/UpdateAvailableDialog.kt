package com.monta.ocpp.emulator.update.view

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.monta.ocpp.emulator.common.components.AppDialog
import com.monta.ocpp.emulator.common.components.OutlineButton
import com.monta.ocpp.emulator.common.components.PrimaryButton
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.update.AppUpdateService
import com.monta.ocpp.emulator.update.model.GithubRelease

/**
 * Release notes dialog. Uses the shared [AppDialog] (built on the plain
 * `Dialog`) rather than Material's AlertDialog, whose intrinsic measurements
 * cause scrollable content to resize the dialog erratically.
 * See https://github.com/JetBrains/compose-jb/issues/2531
 */
@Composable
internal fun UpdateAvailableDialog(
    latestRelease: GithubRelease,
) {
    val appUpdateService: AppUpdateService by injectAnywhere()

    AppDialog(
        onDismissRequest = {},
        title = "New release — ${latestRelease.tagName}",
        modifier = Modifier.width(640.dp),
        dismissButton = {
            OutlineButton(
                onClick = {
                    appUpdateService.clearUpdate()
                },
            ) {
                Text("Close")
            }
        },
        confirmButton = {
            PrimaryButton(
                onClick = {
                    launchThread {
                        latestRelease.let { latestRelease ->
                            appUpdateService.update(latestRelease)
                        }
                    }
                },
            ) {
                Text("Download")
            }
        },
    ) {
        ReleaseNotes(
            body = latestRelease.body,
            modifier = Modifier.fillMaxWidth()
                .heightIn(max = 380.dp),
        )
    }
}

@Composable
private fun ReleaseNotes(
    body: String,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    val lines = remember(body) {
        body.split("\n")
    }

    Box(
        modifier = modifier,
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 12.dp),
        ) {
            items(lines) { block ->
                if (block.startsWith("###")) {
                    Text(
                        text = block.replace("### ", ""),
                        style = MaterialTheme.typography.subtitle2,
                        modifier = Modifier.padding(top = 16.dp),
                    )
                } else {
                    Text(block)
                }
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(lazyListState),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
        )
    }
}
