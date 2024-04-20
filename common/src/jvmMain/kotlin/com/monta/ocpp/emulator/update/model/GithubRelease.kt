package com.monta.ocpp.emulator.update.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class GithubRelease(
    @JsonProperty("url")
    val url: String,
    @JsonProperty("assets_url")
    val assetsUrl: String,
    @JsonProperty("upload_url")
    val uploadUrl: String,
    @JsonProperty("html_url")
    val htmlUrl: String,
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("node_id")
    val nodeId: String,
    @JsonProperty("tag_name")
    val tagName: String,
    @JsonProperty("target_commitish")
    val targetCommitish: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("draft")
    val draft: Boolean,
    @JsonProperty("prerelease")
    val prerelease: Boolean,
    @JsonProperty("created_at")
    val createdAt: OffsetDateTime,
    @JsonProperty("published_at")
    val publishedAt: OffsetDateTime,
    @JsonProperty("assets")
    val assets: List<GithubAsset>,
    @JsonProperty("tarball_url")
    val tarballUrl: String,
    @JsonProperty("zipball_url")
    val zipballUrl: String,
    @JsonProperty("body")
    val body: String
)
