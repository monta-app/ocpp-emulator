package com.monta.ocpp.emulator.platform.update.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class GithubRelease(
    @param:JsonProperty("url")
    val url: String,
    @param:JsonProperty("assets_url")
    val assetsUrl: String,
    @param:JsonProperty("upload_url")
    val uploadUrl: String,
    @param:JsonProperty("html_url")
    val htmlUrl: String,
    @param:JsonProperty("id")
    val id: Int,
    @param:JsonProperty("node_id")
    val nodeId: String,
    @param:JsonProperty("tag_name")
    val tagName: String,
    @param:JsonProperty("target_commitish")
    val targetCommitish: String,
    @param:JsonProperty("name")
    val name: String,
    @param:JsonProperty("draft")
    val draft: Boolean,
    @param:JsonProperty("prerelease")
    val prerelease: Boolean,
    @param:JsonProperty("created_at")
    val createdAt: OffsetDateTime,
    @param:JsonProperty("published_at")
    val publishedAt: OffsetDateTime,
    @param:JsonProperty("assets")
    val assets: List<GithubAsset>,
    @param:JsonProperty("tarball_url")
    val tarballUrl: String,
    @param:JsonProperty("zipball_url")
    val zipballUrl: String,
    @param:JsonProperty("body")
    val body: String,
)
