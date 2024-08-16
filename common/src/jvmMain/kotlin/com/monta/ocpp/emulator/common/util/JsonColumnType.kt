package com.monta.ocpp.emulator.common.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import java.sql.ResultSet

inline fun <reified T : Any> Table.json(
    name: String,
    collate: String? = null,
    eagerLoading: Boolean = false,
    objectMapper: ObjectMapper
): Column<T> = this.json(
    name = name,
    eagerLoading = eagerLoading,
    stringify = { value ->
        objectMapper.writeValueAsString(value)
    },
    parse = { stringValue ->
        objectMapper.readValue(stringValue)
    }
)

fun <T : Any> Table.json(
    name: String,
    eagerLoading: Boolean = false,
    stringify: (T) -> String,
    parse: (String) -> T
): Column<T> {
    return registerColumn(
        name = name,
        type = JsonColumnType(
            eagerLoading = eagerLoading,
            stringify = stringify,
            parse = parse
        )
    )
}

class JsonColumnType<T : Any>(
    private val eagerLoading: Boolean,
    private val stringify: (value: T) -> String,
    private val parse: (stringValue: String) -> T
) : ColumnType<T>() {

    override fun sqlType(): String = "json"

    @Suppress("UNCHECKED_CAST")
    override fun valueFromDB(value: Any): T {
        return when (value) {
            is ByteArray -> parse(String(value).unescapeSqlJsonString())
            else -> value as T
        }
    }

    override fun readObject(
        rs: ResultSet,
        index: Int
    ): Any? {
        val value = rs.getBytes(index)

        return if (eagerLoading && value != null) {
            // return deserialized value
            valueFromDB(value)
        } else {
            // return ByteArray (won't be deserialized)
            value
        }
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
