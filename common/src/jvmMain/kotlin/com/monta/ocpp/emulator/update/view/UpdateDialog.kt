package com.monta.ocpp.emulator.update.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.update.AppUpdateService
import com.monta.ocpp.emulator.update.model.UpdateState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun UpdateDialog() {
    val appUpdateService: AppUpdateService by injectAnywhere()

    val coroutineScope = rememberCoroutineScope()

    var updateState by remember {
        mutableStateOf(UpdateState.None)
    }

    var downloadProgress by remember {
        mutableStateOf(0F)
    }

    coroutineScope.launch {
        appUpdateService.updateState.collectLatest { state ->
            updateState = state
        }
    }

    coroutineScope.launch {
        appUpdateService.downloadProgress.collectLatest { progress ->
            downloadProgress = progress
        }
    }

    when (updateState) {
        UpdateState.None -> {
            // Do nothing :)
        }

        UpdateState.Available -> {
            appUpdateService.latestRelease?.let { latestRelease ->
                UpdateAvailableDialog(
                    latestRelease = latestRelease
                )
            }
        }

        UpdateState.Downloading -> {
            appUpdateService.latestRelease?.let { latestRelease ->
                UpdateProgressDialog(
                    latestRelease = latestRelease,
                    downloadProgress = downloadProgress
                )
            }
        }
    }
}
