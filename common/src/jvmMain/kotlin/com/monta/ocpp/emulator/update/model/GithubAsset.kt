package com.monta.ocpp.emulator.update.model

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubAsset(
    @JsonProperty("id")
    val id: Int, // 91875607
    @JsonProperty("node_id")
    val nodeId: String, // RA_kwDOIrJZFs4FeekX
    @JsonProperty("name")
    val name: String, // mac-OcppEmulator-1.1.4.dmg
    @JsonProperty("label")
    val label: String?,
    @JsonProperty("content_type")
    val contentType: String, // binary/octet-stream
    @JsonProperty("state")
    val state: String, // uploaded
    @JsonProperty("size")
    val size: Int, // 65635314)
)
