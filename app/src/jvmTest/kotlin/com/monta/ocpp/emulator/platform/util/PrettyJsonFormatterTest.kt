package com.monta.ocpp.emulator.platform.util

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.string.shouldContain

class PrettyJsonFormatterTest : DescribeSpec({

    describe("formatJson") {

        it("formats compact json with indentation") {
            val result = PrettyJsonFormatter.formatJson("""{"name":"test","value":42}""")

            result shouldContain "\n"
            result shouldContain "\"name\""
            result shouldContain "42"
        }

        it("handles nested objects") {
            val result = PrettyJsonFormatter.formatJson("""{"outer":{"inner":"value"}}""")

            result shouldContain "\"outer\""
            result shouldContain "\"inner\""
        }

        it("handles arrays") {
            val result = PrettyJsonFormatter.formatJson("""{"items":[1,2,3]}""")

            result shouldContain "1"
            result shouldContain "3"
        }
    }
})
