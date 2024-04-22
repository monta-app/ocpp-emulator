package com.monta.ocpp.emulator.common.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object MontaSerialization {

    /**
     * A singleton version of the object mapper
     */
    val objectMapper: ObjectMapper by lazy {
        getDefaultMapper()
    }

    /**
     * A utility class used for creating the standard ObjectMapper used across all of our services depends on the [withDefaults] function
     *
     * - [serializeNulls] whether the object mapper should return all values whether null or not
     * - [propertyNamingStrategy] the property naming strategy used for the object mapper, if the value isn't override then it will default to default strategy set in the configuration (which is normally how you would want it to be handled :))
     *
     * if the propertyNamingStrategy is left null then it will just behave as normally expected,
     * i.e the casing can vary depending on what the variable is named or whatever the annotation is called,
     * but if the value is set to something non null then the object mapper will enforce this value no matter what
     *
     * <b>important note</b> if you set the property naming strategy it should be derived from the [com.fasterxml.jackson.databind.PropertyNamingStrategies] class
     *
     */
    fun getDefaultMapper(
        serializeNulls: Boolean = false
    ): ObjectMapper {
        return withDefaults(
            objectMapper = ObjectMapper(),
            serializeNulls = serializeNulls
        )
    }

    /**
     * Utility classed used for configuring the ktor serialization stuff but in order to maintain the DRY
     * principles we also reuse when instantiating new ObjectMappers
     *
     * - [objectMapper] the object mapper being configured
     * - [serializeNulls] whether the object mapper should return all values whether null or not
     * - [propertyNamingStrategy] the property naming strategy used for the object mapper, if the value isn't override then it will default to default strategy set in the configuration (which is normally how you would want it to be handled :))
     *
     * if the propertyNamingStrategy is left null then it will just behave as normally expected,
     * i.e the casing can vary depending on what the variable is named or whatever the annotation is called,
     * but if the value is set to something non null then the object mapper will enforce this value no matter what
     *
     * <b>important note</b> if you set the property naming strategy it should be derived from the [com.fasterxml.jackson.databind.PropertyNamingStrategies] class
     *
     */
    fun withDefaults(
        objectMapper: ObjectMapper,
        serializeNulls: Boolean = false
    ): ObjectMapper {
        objectMapper.registerKotlinModule()
        objectMapper.registerModule(JavaTimeModule())
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.setSerializationInclusion(
            if (serializeNulls) {
                JsonInclude.Include.ALWAYS
            } else {
                JsonInclude.Include.NON_NULL
            }
        )
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapper.findAndRegisterModules()
        return objectMapper
    }
}
