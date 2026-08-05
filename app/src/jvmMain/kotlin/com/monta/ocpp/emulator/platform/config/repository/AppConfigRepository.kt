package com.monta.ocpp.emulator.platform.config.repository

import com.monta.ocpp.emulator.platform.config.entity.AppConfigDAO
import com.monta.ocpp.emulator.platform.config.entity.AppConfigTable
import org.jetbrains.exposed.v1.core.eq
import javax.inject.Singleton

@Singleton
class AppConfigRepository {

    fun get(
        key: String,
    ): AppConfigDAO? {
        return AppConfigDAO.find {
            AppConfigTable.key eq key
        }.firstOrNull()
    }

    fun create(
        key: String,
        value: String?,
    ): AppConfigDAO {
        return AppConfigDAO.new {
            this.key = key
            this.value = value
        }
    }

    fun upsert(
        key: String,
        value: String?,
    ): AppConfigDAO {
        val configuration = get(key)

        if (configuration != null) {
            configuration.value = value
            return configuration
        }

        return AppConfigDAO.newInstance(
            key = key,
            value = value,
        )
    }
}
