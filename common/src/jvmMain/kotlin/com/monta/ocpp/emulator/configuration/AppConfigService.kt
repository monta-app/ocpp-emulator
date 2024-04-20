package com.monta.ocpp.emulator.configuration

import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.annotation.Singleton

@Singleton
class AppConfigService(
    private val appConfigRepository: AppConfigRepository
) {

    fun getByKey(
        key: String
    ): String? {
        return transaction {
            appConfigRepository.getByKey(key)
        }?.value
    }

    fun upsert(
        key: String,
        value: String?
    ): AppConfigDAO {
        return transaction {
            appConfigRepository.upsert(key, value)
        }
    }

    fun upsert(
        vararg values: Pair<String, String?>
    ): List<AppConfigDAO> {
        return transaction {
            values.map { (key, value) ->
                appConfigRepository.upsert(key, value)
            }
        }
    }
}
