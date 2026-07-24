package com.monta.ocpp.emulator.update.view

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.update.AppUpdateService
import com.monta.ocpp.emulator.update.model.GithubRelease

/**
 * Release notes dialog built on the plain [Dialog] composable instead of
 * Material's AlertDialog: AlertDialog positions its slots with intrinsic
 * measurements, which scrollable content cannot provide, causing the dialog
 * to resize erratically while scrolling.
 * See https://github.com/JetBrains/compose-jb/issues/2531
 */
@Composable
internal fun UpdateAvailableDialog(
    latestRelease: GithubRelease,
) {
    val appUpdateService: AppUpdateService by injectAnywhere()

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ),
    ) {
        Surface(
            modifier = Modifier.width(640.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = 24.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
            ) {
                Text(
                    text = "New Release - ${latestRelease.tagName}",
                    style = MaterialTheme.typography.h6,
                )
                Spacer(modifier = Modifier.height(16.dp))
                ReleaseNotes(
                    body = latestRelease.body,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.End,
                    ),
                ) {
                    Button(
                        onClick = {
                            appUpdateService.clearUpdate()
                        },
                    ) {
                        Text("Close")
                    }
                    Button(
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
                }
            }
        }
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
