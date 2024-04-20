package com.monta.ocpp.emulator.common.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object PrettyJsonFormatter {

    private val objectMapper = getDefaultNullableMapper()
    private val prettyWriter = getDefaultNullableMapper()
        .writer(
            DefaultPrettyPrinter()
                .withObjectIndenter(
                    DefaultIndenter()
                        .withLinefeed("\n")
                )
                .withArrayIndenter(
                    DefaultIndenter()
                        .withLinefeed("\n")
                )
        )

    fun formatJson(message: String): String {
        return prettyWriter.writeValueAsString(
            objectMapper.readTree(message)
        )
    }

    private fun getDefaultNullableMapper(): ObjectMapper {
        return ObjectMapper().toDefaultMapper(JsonInclude.Include.ALWAYS)
    }

    private fun ObjectMapper.toDefaultMapper(
        jsonInclude: JsonInclude.Include = JsonInclude.Include.NON_NULL
    ): ObjectMapper {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        setSerializationInclusion(jsonInclude)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        findAndRegisterModules()
        return this
    }
}
