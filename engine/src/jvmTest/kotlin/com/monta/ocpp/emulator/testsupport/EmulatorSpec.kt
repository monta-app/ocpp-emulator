package com.monta.ocpp.emulator.testsupport

import io.kotest.core.spec.style.DescribeSpec
import org.koin.core.context.GlobalContext
import org.koin.core.context.stopKoin

abstract class EmulatorSpec(
    body: DescribeSpec.() -> Unit,
) : DescribeSpec({
    useThrowawayDatabase()

    afterEach {
        if (GlobalContext.getOrNull() != null) {
            stopKoin()
        }
    }

    body()
})
