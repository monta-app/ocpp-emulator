package com.monta.ocpp.emulator.common.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ColumnType
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.statements.api.RowApi

inline fun <reified T : Any> Table.json(
    name: String,
    collate: String? = null,
    eagerLoading: Boolean = false,
    objectMapper: ObjectMapper,
): Column<T> = this.json(
    name = name,
    eagerLoading = eagerLoading,
    stringify = { value ->
        objectMapper.writeValueAsString(value)
    },
    parse = { stringValue ->
        objectMapper.readValue(stringValue)
    },
)

fun <T : Any> Table.json(
    name: String,
    eagerLoading: Boolean = false,
    stringify: (T) -> String,
    parse: (String) -> T,
): Column<T> {
    return registerColumn(
        name = name,
        type = JsonColumnType(
            eagerLoading = eagerLoading,
            stringify = stringify,
            parse = parse,
        ),
    )
}

class JsonColumnType<T : Any>(
    private val eagerLoading: Boolean,
    private val stringify: (value: T) -> String,
    private val parse: (stringValue: String) -> T,
) : ColumnType<T>() {

    override fun sqlType(): String = "json"

    @Suppress("UNCHECKED_CAST")
    override fun valueFromDB(
        value: Any,
    ): T {
        return when (value) {
            is ByteArray -> parse(value.decodeToString().unescapeSqlJsonString())
            else -> value as T
        }
    }

    override fun readObject(
        rs: RowApi,
        index: Int,
    ): Any? {
        val value = rs.getObject(index, ByteArray::class.java)

        return if (eagerLoading && value != null) {
            // return deserialized value
            valueFromDB(value)
        } else {
            // return ByteArray (won't be deserialized)
            value
        }
    }

    override fun notNullValueToDB(
        value: T,
    ): Any = stringify(value)

    override fun valueToString(
        value: T?,
    ): String = when (value) {
        is Iterable<*> -> nonNullValueToString(value)
        else -> super.valueToString(value)
    }
}

/**
 * MySql returns json in the current format "{\"hello\":\"goodbye\"}"
 * this function turns that into {"hello":"goodbye"}
 * which jackson can actually read
 */
internal fun String.unescapeSqlJsonString(): String {
    if (startsWith("\"") && endsWith("\"")) {
        return substring(1, length - 1).replace("\\\"", "\"")
    }
    return this
}
