package com.monta.ocpp.emulator.user

import io.sentry.Sentry
import io.sentry.protocol.User
import org.koin.core.annotation.Singleton
import org.slf4j.LoggerFactory

@Singleton
class AnalyticsHelper(
    private val appUserService: AppUserService
) {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(AnalyticsHelper::class.java)
    }

    fun initSentry() {
        try {
            Sentry.setUser(
                User().apply {
                    id = appUserService.getUserId()
                }
            )
        } catch (e: Exception) {
            logger.warn("[sentry] failed to set user", e)
        }
    }
}
