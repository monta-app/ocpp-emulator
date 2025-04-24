package com.monta.ocpp.emulator.common.util

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private val logger = KotlinLogging.logger {}

@OptIn(DelicateCoroutinesApi::class)
fun launchThread(
    restart: Boolean = false,
    block: suspend () -> Unit
): Job {
    return GlobalScope.launch(if (restart) restartingHandler(block) else exceptionLogger) {
        block()
    }
}

private fun restartingHandler(
    block: suspend () -> Unit
): CoroutineExceptionHandler {
    return CoroutineExceptionHandler { coroutineContext, throwable ->
        logger.error("error in $coroutineContext", throwable)
        logger.warn { "restarting thread..." }
        launchThread(true) { block() }
    }
}

private val exceptionLogger = CoroutineExceptionHandler { coroutineContext, throwable ->
    logger.error("error in $coroutineContext", throwable)
}
