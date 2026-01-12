package com.monta.ocpp.emulator.chargepoint.view.createchargepoint

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import com.monta.ocpp.emulator.common.components.BackButton
import com.monta.ocpp.emulator.common.util.injectAnywhere
import com.monta.ocpp.emulator.common.view.NavigationViewModel
import com.monta.ocpp.emulator.interceptor.view.BasePage

@Composable
fun CreateChargePointPage(
    chargePoint: ChargePointDAO?,
) {
    val navigationViewModel: NavigationViewModel by injectAnywhere()

    BasePage(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (chargePoint == null) {
                            "Create Charge Point"
                        } else {
                            "Edit Charge Point"
                        },
                    )
                },
                navigationIcon = {
                    BackButton {
                        navigationViewModel.chargePointsScreen()
                    }
                },
            )
        },
    ) {
        val viewModel: ChargePointFormViewModel by injectAnywhere()
        ChargePointForm(viewModel, chargePoint)
    }
}
