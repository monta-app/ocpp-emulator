// GENERATED CODE - DO NOT EDIT BY HAND.
// Regenerate with scripts/codegen/generate_ocpp_v21_protocol.py

package com.monta.ocpp.emulator.ocpp.v21.protocol.messages.common

data class FixedPFGet(
    val fixedPF: FixedPF,
    /** Id of setting. */
    val id: String,
    /** True if setting is a default control. */
    val isDefault: Boolean,
    /** True if this setting is superseded by a lower priority setting. */
    val isSuperseded: Boolean,
    val customData: CustomData? = null,
)
