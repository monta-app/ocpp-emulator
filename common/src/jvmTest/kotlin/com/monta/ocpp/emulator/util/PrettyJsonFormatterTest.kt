package com.monta.ocpp.emulator.util

import com.monta.ocpp.emulator.common.util.PrettyJsonFormatter
import com.monta.ocpp.emulator.common.util.randomString
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PrettyJsonFormatterTest {

    @Test
    fun `formatJson formats compact json with indentation`() {
        val input = """{"name":"test","value":42}"""
        val result = PrettyJsonFormatter.formatJson(input)
        assertTrue(result.contains("\n"), "Output should contain newlines")
        assertTrue(result.contains("\"name\""), "Output should contain the name field")
        assertTrue(result.contains("42"), "Output should contain the value")
    }

    @Test
    fun `formatJson handles nested objects`() {
        val input = """{"outer":{"inner":"value"}}"""
        val result = PrettyJsonFormatter.formatJson(input)
        assertTrue(result.contains("\"outer\""))
        assertTrue(result.contains("\"inner\""))
    }

    @Test
    fun `formatJson handles arrays`() {
        val input = """{"items":[1,2,3]}"""
        val result = PrettyJsonFormatter.formatJson(input)
        assertTrue(result.contains("1"))
        assertTrue(result.contains("3"))
    }
}

class ExtensionsTest {

    @Test
    fun `randomString returns string of correct length`() {
        val result = randomString(10)
        assertEquals(10, result.length)
    }

    @Test
    fun `randomString returns only alphanumeric uppercase characters`() {
        val result = randomString(100)
        assertTrue(result.all { it.isLetterOrDigit() && (it.isUpperCase() || it.isDigit()) })
    }

    @Test
    fun `randomString returns different values`() {
        val a = randomString(20)
        val b = randomString(20)
        assertTrue(a != b, "Two random strings should differ")
    }
}
