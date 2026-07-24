package com.monta.ocpp.emulator.update.model

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubAsset(
    @param:JsonProperty("id")
    val id: Int, // 91875607
    @param:JsonProperty("node_id")
    val nodeId: String, // RA_kwDOIrJZFs4FeekX
    @param:JsonProperty("name")
    val name: String, // mac-OcppEmulator-1.1.4.dmg
    @param:JsonProperty("label")
    val label: String?,
    @param:JsonProperty("content_type")
    val contentType: String, // binary/octet-stream
    @param:JsonProperty("state")
    val state: String, // uploaded
    @param:JsonProperty("size")
    val size: Int, // 65635314)
)
