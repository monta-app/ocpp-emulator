package com.monta.ocpp.emulator.platform.util

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ExtensionsTest : DescribeSpec({

    describe("randomString") {

        it("returns a string of the requested length") {
            val length = randomString(10).length

            length shouldBe 10
        }

        it("returns only uppercase letters and digits") {
            val result = randomString(100)

            val isAllUppercaseOrDigits = result.all { character ->
                character.isLetterOrDigit() && (character.isUpperCase() || character.isDigit())
            }

            isAllUppercaseOrDigits shouldBe true
        }

        it("returns a different value on each call") {
            val first = randomString(20)
            val second = randomString(20)

            first shouldNotBe second
        }
    }
})
