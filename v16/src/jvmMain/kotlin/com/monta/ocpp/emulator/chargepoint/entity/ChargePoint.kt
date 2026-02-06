package com.monta.ocpp.emulator.chargepoint.entity

import com.monta.library.ocpp.common.session.OcppSession
import com.monta.library.ocpp.v16.core.ChargePointErrorCode
import com.monta.library.ocpp.v16.core.ChargePointStatus
import com.monta.library.ocpp.v16.core.DataTransferRequest
import com.monta.library.ocpp.v16.firmware.DiagnosticsStatusNotificationStatus
import com.monta.library.ocpp.v16.firmware.FirmwareStatusNotificationStatus
import com.monta.ocpp.emulator.chargepoint.model.ChargePointConfiguration
import com.monta.ocpp.emulator.chargepoint.model.ChargePointMode
import com.monta.ocpp.emulator.chargepoint.model.LocalAuthList
import com.monta.ocpp.emulator.chargepoint.model.OcppVersion
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorDAO
import com.monta.ocpp.emulator.chargepointconnector.entity.ChargePointConnectorTable
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransaction
import com.monta.ocpp.emulator.chargepointtransaction.entity.ChargePointTransactionDAO
import com.monta.ocpp.emulator.common.idValue
import com.monta.ocpp.emulator.common.util.MontaSerialization
import com.monta.ocpp.emulator.common.util.json
import com.monta.ocpp.emulator.logger.Loggable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant

// Table Definition
object ChargePointTable : LongIdTable("charge_point") {

    val name = varchar("name", 512)
    val identity = varchar("identity", 512).uniqueIndex()

    val operationMode = enumerationByName("operation_mode", 128, ChargePointMode::class)
    val ocppVersion = enumerationByName("ocpp_version", 128, OcppVersion::class)
    val connected = bool("connected")
    val bootedAt = timestamp("booted_at")
        .nullable()
        .default(null)

    // Info
    val brand = varchar("brand", 256)
    val model = varchar("model", 256)
    val serial = varchar("serial", 256)
    val firmware = varchar("firmware", 256)

    // Settings
    val basicAuthPassword = varchar("basic_auth_password", 1024).nullable()
    val ocppUrl = varchar("ocpp_url", 1024)
    val apiUrl = varchar("api_url", 1024)
    val maxKw = double("max_kw")

    val messageCount = integer("message_count")
        .default(0)
    val averageLatencyMillis = long("average_latency_millis")
        .default(0)

    // Heartbeat
    val heartbeatAt = timestamp("heartbeat_at")

    // Status
    val status = enumerationByName("status", 128, ChargePointStatus::class)
    val statusAt = timestamp("status_at")

    val errorCode = enumerationByName("error_code", 256, ChargePointErrorCode::class)

    val firmwareStatus = enumerationByName("firmware_status", 256, FirmwareStatusNotificationStatus::class)
    val firmwareStatusAt = timestamp("firmware_status_at")

    val diagnosticsStatus = enumerationByName("diagnostics_status", 256, DiagnosticsStatusNotificationStatus::class)
    val diagnosticsStatusAt = timestamp("diagnostics_status_at")

    /* 5 lines of 80 characters + newline */
    val displayText = varchar("display_text", 5 * (80 + 1)).default("\n\n\n\n")

    // Config
    val configuration = json<ChargePointConfiguration>(
        name = "configuration",
        objectMapper = MontaSerialization.getDefaultMapper(),
    )
    val localAuthList = json<LocalAuthList>(
        name = "local_auth_list",
        objectMapper = MontaSerialization.getDefaultMapper(),
    )
}

// DAO
class ChargePointDAO(
    id: EntityID<Long>,
) : LongEntity(id), Loggable {

    companion object : LongEntityClass<ChargePointDAO>(ChargePointTable) {
        fun newInstance(
            name: String,
            identity: String,
            password: String?,
            ocppUrl: String,
            apiUrl: String,
            firmware: String,
            maxKw: Double,
        ): ChargePointDAO {
            return ChargePointDAO.new {
                this.name = name
                this.identity = identity.trim().uppercase()
                //
                this.operationMode = ChargePointMode.Manual
                this.ocppVersion = OcppVersion.V16
                this.bootedAt = null
                // Info
                this.brand = "Monta"
                this.model = "E-Emulator"
                this.serial = "MontaEmulator"
                this.firmware = firmware
                // Settings
                this.connected = false
                this.basicAuthPassword = password
                this.ocppUrl = ocppUrl
                this.apiUrl = apiUrl
                this.maxKw = maxKw

                this.heartbeatAt = Instant.now()

                this.status = ChargePointStatus.Unavailable
                this.statusAt = Instant.now()

                this.errorCode = ChargePointErrorCode.NoError

                this.firmwareStatus = FirmwareStatusNotificationStatus.Idle
                this.firmwareStatusAt = Instant.now()
                this.diagnosticsStatus = DiagnosticsStatusNotificationStatus.Idle
                this.diagnosticsStatusAt = Instant.now()

                this.configuration = ChargePointConfiguration()
                this.localAuthList = LocalAuthList()
            }
        }
    }

    var name by ChargePointTable.name
    var identity by ChargePointTable.identity

    var operationMode by ChargePointTable.operationMode
    var ocppVersion by ChargePointTable.ocppVersion
    var connected by ChargePointTable.connected
    var bootedAt by ChargePointTable.bootedAt

    var brand by ChargePointTable.brand
    var model by ChargePointTable.model
    var serial by ChargePointTable.serial
    var firmware by ChargePointTable.firmware

    var basicAuthPassword by ChargePointTable.basicAuthPassword
    var ocppUrl by ChargePointTable.ocppUrl
    var apiUrl by ChargePointTable.apiUrl
    var maxKw by ChargePointTable.maxKw

    var averageLatencyMillis by ChargePointTable.averageLatencyMillis
    var messageCount by ChargePointTable.messageCount

    var heartbeatAt by ChargePointTable.heartbeatAt

    var status by ChargePointTable.status
    var statusAt by ChargePointTable.statusAt

    var errorCode by ChargePointTable.errorCode

    var firmwareStatus by ChargePointTable.firmwareStatus
    var firmwareStatusAt by ChargePointTable.firmwareStatusAt

    var diagnosticsStatus by ChargePointTable.diagnosticsStatus
    var diagnosticsStatusAt by ChargePointTable.diagnosticsStatusAt

    var displayText by ChargePointTable.displayText

    var configuration by ChargePointTable.configuration
        private set
    var localAuthList by ChargePointTable.localAuthList

    val connectors by ChargePointConnectorDAO referrersOn ChargePointConnectorTable.chargePointId

    val canPerformAction: Boolean
        get() {
            if (firmwareStatus == FirmwareStatusNotificationStatus.Downloading) {
                return false
            }
            if (firmwareStatus == FirmwareStatusNotificationStatus.Installing) {
                return false
            }
            return true
        }

    fun getActiveTransactions(): List<ChargePointTransactionDAO> {
        return transaction {
            ChargePointTransactionDAO.find {
                (ChargePointTransaction.chargePointId eq this@ChargePointDAO.id) and
                    (ChargePointTransaction.endTime eq null)
            }.toList()
        }
    }

    fun getConnectors(): List<ChargePointConnectorDAO> {
        return transaction {
            connectors.toList()
        }
    }

    fun getConnector(
        connectorId: Int,
    ): ChargePointConnectorDAO {
        return transaction {
            val connector = connectors.firstOrNull { it.position == connectorId }
            if (connector != null) return@transaction connector
            return@transaction ChargePointConnectorDAO.newInstance(
                chargePointId = this@ChargePointDAO.idValue,
                chargePointIdentity = this@ChargePointDAO.identity,
                position = connectorId,
                status = ChargePointStatus.Available,
                errorCode = ChargePointErrorCode.NoError,
                maxKw = maxKw,
            )
        }
    }

    val sessionInfo: OcppSession.Info by lazy {
        OcppSession.Info("", identity)
    }

    fun updateConfiguration(
        update: ChargePointConfiguration.() -> Unit,
    ) {
        val configuration = ChargePointConfiguration()
        configuration.putAll(this.configuration)
        update(configuration)
        this.configuration = configuration
    }

    fun handleDataTransferRequest(
        request: DataTransferRequest,
    ): Boolean {
        return when (request.vendorId) {
            "com.monta" -> handleMontaLCDMessage(request)
            else -> false
        }
    }

    private fun handleMontaLCDMessage(
        request: DataTransferRequest,
    ): Boolean {
        var displayLines = displayText.split("\n").toMutableList()
        when (request.messageId) {
            "SmartChargingEnabled" -> displayLines[0] = if (request.data == "true") "Smart Charging" else "Charging"
            "StartTime" -> displayLines[2] = "Start at: ${request.data}"
            "EndTime" -> displayLines[3] = "Will be finished at: ${request.data}"
            "SoC" -> displayLines[4] = "Battery at: ${request.data}%"
            "ClearDisplay" -> displayLines = List(5) { "" }.toMutableList()
            else -> return false
        }

        displayText = displayLines.joinToString("\n")
        return true
    }

    override fun chargePointId(): Long {
        return this.idValue
    }

    override fun connectorPosition(): Int {
        return 0
    }
}
