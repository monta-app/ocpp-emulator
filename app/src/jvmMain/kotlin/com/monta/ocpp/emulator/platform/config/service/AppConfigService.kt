package com.monta.ocpp.emulator.platform.config.service

import com.monta.ocpp.emulator.platform.config.entity.AppConfigDAO
import com.monta.ocpp.emulator.platform.config.repository.AppConfigRepository
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import javax.inject.Singleton

@Singleton
class AppConfigService(
    private val appConfigRepository: AppConfigRepository,
) {

    fun getOrCreate(
        key: String,
        defaultValue: String,
    ): AppConfigDAO {
        return transaction {
            appConfigRepository.get(key) ?: appConfigRepository.create(key, defaultValue)
        }
    }

    fun getByKey(
        key: String,
    ): String? {
        return transaction {
            appConfigRepository.get(key)
        }?.value
    }

    fun upsert(
        key: String,
        value: String?,
    ): AppConfigDAO {
        return transaction {
            appConfigRepository.upsert(key, value)
        }
    }

    fun upsert(
        vararg values: Pair<String, String?>,
    ): List<AppConfigDAO> {
        return transaction {
            values.map { (key, value) ->
                appConfigRepository.upsert(key, value)
            }
        }
    }
}
