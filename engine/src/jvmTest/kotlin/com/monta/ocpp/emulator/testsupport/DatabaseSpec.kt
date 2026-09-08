package com.monta.ocpp.emulator.testsupport

import io.kotest.core.spec.style.DescribeSpec

abstract class DatabaseSpec(
    body: DescribeSpec.() -> Unit,
) : DescribeSpec({
    useThrowawayDatabase()
    body()
})
