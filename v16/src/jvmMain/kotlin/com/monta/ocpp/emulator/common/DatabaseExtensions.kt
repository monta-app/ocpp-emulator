package com.monta.ocpp.emulator.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.jetbrains.exposed.dao.EntityChange
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass

private val logger = KotlinLogging.logger {}

val LongEntity.idValue: Long
    get() = id.value

fun <T> createDatabaseListener(
    coroutineScope: CoroutineScope,
    entityClass: LongEntityClass<*>,
    id: Long? = null,
    triggerUpdate: () -> T?
): Flow<T> = channelFlow {
    triggerUpdate()?.let { value ->
        send(value)
    }

    val listener: (EntityChange) -> Unit = { entityChange ->
        if (entityChange.entityClass == entityClass) {
            if (id != null) {
                if (entityChange.entityId.value == id) {
                    coroutineScope.launch {
                        triggerUpdate()?.let { value ->
                            send(value)
                        }
                    }
                }
            } else {
                coroutineScope.launch {
                    triggerUpdate()?.let { value ->
                        send(value)
                    }
                }
            }
        }
    }

    logger.debug("hook started entityClass=${entityClass::class.java.name}, id=$id")
    EntityHook.subscribe(listener)

    awaitClose {
        logger.debug("closing hook entityClass=${entityClass::class.java.name}, id=$id")
        EntityHook.unsubscribe(listener)
    }
}
