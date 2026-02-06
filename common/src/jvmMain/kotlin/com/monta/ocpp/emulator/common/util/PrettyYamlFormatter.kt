package com.monta.ocpp.emulator.common.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object PrettyYamlFormatter {
    private val nullableYamlMapper: ObjectMapper = ObjectMapper(YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
        .registerKotlinModule()
        .registerModule(JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setSerializationInclusion(JsonInclude.Include.ALWAYS)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .findAndRegisterModules()

    private val yamlWriter: ObjectWriter = nullableYamlMapper
        .writerWithDefaultPrettyPrinter()

    fun <T> readYaml(
        yaml: String,
        clazz: Class<T>,
    ): T {
        return nullableYamlMapper.readerFor(clazz).readValue(yaml)
    }

    fun writeYaml(
        value: Any,
    ): String {
        return yamlWriter.writeValueAsString(value)
    }
}
