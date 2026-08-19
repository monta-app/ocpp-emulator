package com.monta.ocpp.emulator.platform.util

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CliArgsTest {

    @Test
    fun `no arguments does not enable integration mode`() {
        val result = parseCliArgs(emptyArray())

        assertEquals(CliArgs(integrationMode = false, databaseName = null), result)
    }

    @Test
    fun `-integration enables integration mode with default database`() {
        val result = parseCliArgs(arrayOf("-integration"))

        assertEquals(CliArgs(integrationMode = true, databaseName = null), result)
    }

    @Test
    fun `-integration --db sets a custom database name`() {
        val result = parseCliArgs(arrayOf("-integration", "--db", "scenario-1.db"))

        assertEquals(CliArgs(integrationMode = true, databaseName = "scenario-1.db"), result)
    }

    @Test
    fun `--db without -integration is rejected`() {
        val exception = assertFails { parseCliArgs(arrayOf("--db", "scenario-1.db")) }

        assertTrue(exception is CliArgsException)
    }

    @Test
    fun `--db without a value is rejected`() {
        val exception = assertFails { parseCliArgs(arrayOf("-integration", "--db")) }

        assertTrue(exception is CliArgsException)
    }

    @Test
    fun `unknown argument is rejected`() {
        val exception = assertFails { parseCliArgs(arrayOf("-bogus")) }

        assertTrue(exception is CliArgsException)
    }

    @Test
    fun `help flags return null without throwing`() {
        assertNull(parseCliArgs(arrayOf("-?")))
        assertNull(parseCliArgs(arrayOf("-h")))
        assertNull(parseCliArgs(arrayOf("--help")))
    }
}
