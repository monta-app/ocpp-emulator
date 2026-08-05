package com.monta.ocpp.emulator.chargepoint.core.ui.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import com.monta.ocpp.emulator.chargepoint.core.entity.ChargePointDAO
import com.monta.ocpp.emulator.navigation.service.Navigator
import com.monta.ocpp.emulator.platform.util.injectAnywhere
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

/**
 * Create/edit charge point, rendered as a Navigation Compose `dialog` destination.
 *
 * When [chargePointId] is non-null we load the existing [ChargePointDAO] and pre-fill the form
 * (edit mode); a null id is create mode. The form view model is [remember]ed so it survives
 * recomposition but is recreated each time the dialog is opened (the destination leaves
 * composition when dismissed), which resets the form without the old manual `initialized` guard
 * having to reach across screen visits.
 */
@Composable
fun CreateChargePointDialog(
    chargePointId: Long?,
) {
    val navigator: Navigator by injectAnywhere()
    val viewModel by remember { injectAnywhere<ChargePointFormViewModel>() }

    val chargePoint by produceState<ChargePointDAO?>(initialValue = null, chargePointId) {
        value = chargePointId?.let { id ->
            transaction { ChargePointDAO.findById(id) }
        }
    }

    // In edit mode, wait until the charge point has loaded before showing the form.
    if (chargePointId != null && chargePoint == null) {
        return
    }

    ChargePointForm(
        viewModel = viewModel,
        chargePoint = chargePoint,
        onClose = {
            navigator.back()
        },
    )
}
