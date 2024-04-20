package com.monta.ocpp.emulator.configuration

import org.koin.core.annotation.Singleton

@Singleton
class AppConfigRepository {
    fun getByKey(
        key: String
    ): AppConfigDAO? {
        return AppConfigDAO.find {
            AppConfigTable.key eq key
        }.firstOrNull()
    }

    fun upsert(
        key: String,
        value: String?
    ): AppConfigDAO {
        val configuration = getByKey(key)

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
