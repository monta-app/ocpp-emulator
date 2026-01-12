package com.monta.ocpp.emulator.user

import io.sentry.Sentry
import io.sentry.protocol.User
import javax.inject.Singleton
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
        val sentryDsn: String? = System.getProperty("sentry.dsn")
        if (sentryDsn.isNullOrEmpty()) {
            logger.warn("sentry.dsn is not set")
            return
        }
        try {
            Sentry.init { options ->
                options.dsn = sentryDsn
            }
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
