package com.monta.ocpp.emulator.platform.util

private const val FLAG_INTEGRATION = "-integration"
private const val FLAG_DB = "--db"
private val HELP_FLAGS = setOf("-?", "-h", "--help")

private val HELP_TEXT = """
    Usage: OcppEmulator [options]

    Options:
      (none)              Start normally with the GUI. The control socket used for
                          integration testing is not started (default, unchanged
                          behavior).
      -integration        Start the control socket (see docs/cli/cli-plan.md) so an
                          external test suite can drive this instance without the GUI.
      --db <name>         Only valid together with -integration. Connect to the SQLite
                          database file <name> under ~/monta/ instead of the default
                          (app.db), creating it if it doesn't exist yet. Lets separate
                          integration test scenarios keep separate charge point
                          configurations.
      -?, -h, --help      Show this help and exit.
""".trimIndent()

data class CliArgs(
    val integrationMode: Boolean = false,
    val databaseName: String? = null,
)

class CliArgsException(
    message: String,
) : Exception(message)

/**
 * Parses process arguments into [CliArgs]. Returns `null` when help was requested (and
 * already printed) — the caller should exit without starting the app. Throws
 * [CliArgsException] on invalid input; the caller should print [Exception.message] to
 * stderr and exit non-zero.
 */
fun parseCliArgs(
    args: Array<String>,
): CliArgs? {
    var integrationMode = false
    var databaseName: String? = null

    var index = 0
    while (index < args.size) {
        when (val arg = args[index]) {
            in HELP_FLAGS -> {
                println(HELP_TEXT)
                return null
            }
            FLAG_INTEGRATION -> {
                integrationMode = true
            }
            FLAG_DB -> {
                index++
                databaseName = args.getOrNull(index)
                    ?: throw CliArgsException("$FLAG_DB requires a database name")
            }
            else -> throw CliArgsException("unknown argument: $arg\n\n$HELP_TEXT")
        }
        index++
    }

    if (databaseName != null && !integrationMode) {
        throw CliArgsException("$FLAG_DB is only valid together with $FLAG_INTEGRATION")
    }

    return CliArgs(
        integrationMode = integrationMode,
        databaseName = databaseName,
    )
}
