package com.monta.ocpp.emulator.platform.database.extension

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import org.jetbrains.exposed.v1.dao.EntityChange
import org.jetbrains.exposed.v1.dao.EntityHook
import org.jetbrains.exposed.v1.dao.LongEntity
import org.jetbrains.exposed.v1.dao.LongEntityClass

private val logger = KotlinLogging.logger {}

val LongEntity.idValue: Long
    get() = id.value

/**
 * Cold [Flow] that emits [triggerUpdate]'s result once up front and again whenever an Exposed entity
 * of [entityClass] (optionally narrowed to a single [id]) changes.
 *
 * The re-emit `send`s are launched on the [channelFlow] builder's own `ProducerScope`, so their
 * lifetime is tied to the flow collector — no external [kotlinx.coroutines.CoroutineScope] has to be
 * threaded in, and the launched coroutines are cancelled when the collector goes away.
 */
fun <T> createDatabaseListener(
    entityClass: LongEntityClass<*>,
    id: Long? = null,
    triggerUpdate: () -> T?,
): Flow<T> = channelFlow {
    triggerUpdate()?.let { value ->
        send(value)
    }

    val listener: (EntityChange) -> Unit = { entityChange ->
        if (entityChange.entityClass == entityClass && (id == null || entityChange.entityId.value == id)) {
            launch {
                triggerUpdate()?.let { value ->
                    send(value)
                }
            }
        }
    }

    logger.debug { "hook started entityClass=${entityClass::class.java.name}, id=$id" }
    EntityHook.subscribe(listener)

    awaitClose {
        logger.debug { "closing hook entityClass=${entityClass::class.java.name}, id=$id" }
        EntityHook.unsubscribe(listener)
    }
}
