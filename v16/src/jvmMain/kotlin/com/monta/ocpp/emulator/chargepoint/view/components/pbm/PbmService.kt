package com.monta.ocpp.emulator.chargepoint.view.components.pbm

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.monta.ocpp.emulator.chargepoint.entity.ChargePointDAO
import io.nayuki.qrcodegen.QrCode
import kotlinx.coroutines.flow.MutableStateFlow
import java.awt.image.BufferedImage

object PbmService {
    var showUrlQR = MutableStateFlow(false)
    var showSerialQR = MutableStateFlow(false)

    fun createQrCode(
        chargePoint: ChargePointDAO,
        showUrlQR: Boolean
    ): ImageBitmap {
        return generateQrCode(
            url = if (showUrlQR) {
                getPbmUrl(chargePoint)
            } else {
                "serial=${chargePoint.identity}"
            }
        )
    }

    private fun generateQrCode(url: String): ImageBitmap {
        return QrCode.encodeText(url, QrCode.Ecc.MEDIUM)
            .toImage()
            .toComposeImageBitmap()
    }

    private fun getPbmUrl(
        chargePoint: ChargePointDAO
    ): String {
        var apiUrl = chargePoint.apiUrl

        if (!apiUrl.endsWith("/")) {
            apiUrl += "/"
        }

        return "${apiUrl}d/powered-by-monta?model=${chargePoint.model}&serial=${chargePoint.identity}"
    }

    private fun QrCode.toImage(
        scale: Int = 10,
        border: Int = 3,
        lightColor: Int = 0xFFFFFF,
        darkColor: Int = 0x000000
    ): BufferedImage {
        val bufferedImage = BufferedImage(
            (size + border * 2) * scale,
            (size + border * 2) * scale,
            BufferedImage.TYPE_INT_RGB
        )

        for (y in 0 until bufferedImage.height) {
            for (x in 0 until bufferedImage.width) {
                val color: Boolean = getModule(x / scale - border, y / scale - border)
                bufferedImage.setRGB(x, y, if (color) darkColor else lightColor)
            }
        }

        return bufferedImage
    }
}
