package com.monta.ocpp.emulator.configuration

import org.koin.core.annotation.Singleton

@Singleton
class AppConfigRepository {

    fun get(
        key: String
    ): AppConfigDAO? {
        return AppConfigDAO.find {
            AppConfigTable.key eq key
        }.firstOrNull()
    }

    fun create(
        key: String,
        value: String?
    ): AppConfigDAO {
        return AppConfigDAO.new {
            this.key = key
            this.value = value
        }
    }

    fun upsert(
        key: String,
        value: String?
    ): AppConfigDAO {
        val configuration = get(key)

        if (configuration != null) {
            configuration.value = value
            return configuration
        }

        return AppConfigDAO.newInstance(
            key = key,
            value = value
        )
    }
}
