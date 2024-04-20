package com.monta.ocpp.emulator.common.util

import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun randomString(length: Int) = ThreadLocalRandom.current()
    .ints(length.toLong(), 0, charPool.size)
    .asSequence()
    .map {
        charPool[it].uppercase()
    }
    .joinToString("")
