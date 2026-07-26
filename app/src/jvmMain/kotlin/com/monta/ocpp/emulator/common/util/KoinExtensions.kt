package com.monta.ocpp.emulator.common.util

import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.mp.KoinPlatform.getKoin

/**
 * Small utility function that allows you to pull a koin dependency from any where in the application
 */
inline fun <reified T : Any> injectAnywhere(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
): Lazy<T> {
    return lazy {
        getKoin().get(qualifier, parameters)
    }
}
