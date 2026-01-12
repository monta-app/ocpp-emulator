package com.monta.ocpp.emulator.user

import com.monta.ocpp.emulator.configuration.AppConfigService
import java.util.UUID
import javax.inject.Singleton

@Singleton
class AppUserService(
    private val appConfigService: AppConfigService,
) {

    companion object {
        private const val USER_ID_KEY = "user_id"
    }

    fun getUserId(): String {
        val userIdConfig = appConfigService.getOrCreate(
            key = USER_ID_KEY,
            defaultValue = UUID.randomUUID().toString(),
        )
        return userIdConfig.value ?: UUID.randomUUID().toString()
    }
}
