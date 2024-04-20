package com.monta.ocpp.emulator.update.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class GithubRelease(
    @JsonProperty("url")
    val url: String, // https://api.github.com/repos/monta-app/tool-ocpp-emulator/releases/89118842
    @JsonProperty("assets_url")
    val assetsUrl: String, // https://api.github.com/repos/monta-app/tool-ocpp-emulator/releases/89118842/assets
    @JsonProperty("upload_url")
    val uploadUrl: String, // https://uploads.github.com/repos/monta-app/tool-ocpp-emulator/releases/89118842/assets{?name,label}
    @JsonProperty("html_url")
    val htmlUrl: String, // https://github.com/monta-app/tool-ocpp-emulator/releases/tag/v1.1.4
    @JsonProperty("id")
    val id: Int, // 89118842
    @JsonProperty("node_id")
    val nodeId: String, // RE_kwDOIrJZFs4FT9h6
    @JsonProperty("tag_name")
    val tagName: String, // v1.1.4
    @JsonProperty("target_commitish")
    val targetCommitish: String, // develop
    @JsonProperty("name")
    val name: String,
    @JsonProperty("draft")
    val draft: Boolean, // false
    @JsonProperty("prerelease")
    val prerelease: Boolean, // false
    @JsonProperty("created_at")
    val createdAt: OffsetDateTime, // 2023-01-16T12:05:01Z
    @JsonProperty("published_at")
    val publishedAt: OffsetDateTime, // 2023-01-16T12:07:59Z
    @JsonProperty("assets")
    val assets: List<GithubAsset>,
    @JsonProperty("tarball_url")
    val tarballUrl: String, // https://api.github.com/repos/monta-app/tool-ocpp-emulator/tarball/v1.1.4
    @JsonProperty("zipball_url")
    val zipballUrl: String, // https://api.github.com/repos/monta-app/tool-ocpp-emulator/zipball/v1.1.4
    @JsonProperty("body")
    val body: String // ### 🚀 Feature- added security event support- security profile 1 support- include Current.Import in MeterValues### 🐛 Fix- measurand string### 🧹 Chore- bumped version to 1.1.4- applied linter- UI tweaks
)
