package com.monta.ocpp.emulator.platform.util

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ExtensionsTest : DescribeSpec({

    describe("randomString") {

        it("returns a string of the requested length") {
            randomString(10).length shouldBe 10
        }

        it("returns only uppercase letters and digits") {
            val result = randomString(100)

            result.all { character ->
                character.isLetterOrDigit() && (character.isUpperCase() || character.isDigit())
            } shouldBe true
        }

        it("returns a different value on each call") {
            randomString(20) shouldNotBe randomString(20)
        }
    }
})
