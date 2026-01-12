package com.monta.ocpp.emulator.update

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.monta.ocpp.emulator.common.util.launchThread
import com.monta.ocpp.emulator.update.model.GithubAsset
import com.monta.ocpp.emulator.update.model.GithubRelease
import com.monta.ocpp.emulator.update.model.OS
import com.monta.ocpp.emulator.update.model.UpdateState
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import net.swiftzer.semver.SemVer
import javax.inject.Singleton
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.system.exitProcess

@Singleton
class AppUpdateService {

    companion object {
        private const val BASE_URL = "https://api.github.com/repos/monta-app/ocpp-emulator"
    }

    private val logger = KotlinLogging.logger {}

    private val mutex = Mutex()

    val updateState: MutableStateFlow<UpdateState> = MutableStateFlow(UpdateState.None)
    val downloadProgress: MutableStateFlow<Float> = MutableStateFlow(0F)
    var latestRelease: GithubRelease? = null

    private val client = HttpClient(CIO) {
        if (!System.getenv("DEBUG").isNullOrBlank()) {
            install(Logging)
        }
        install(ContentNegotiation) {
            jackson {
                registerKotlinModule()
                registerModule(JavaTimeModule())
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
                disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                findAndRegisterModules()
            }
        }
    }

    private val downloadClient = HttpClient(CIO) {
        defaultRequest {
            headers {
                append("Accept", "application/octet-stream")
                append("X-GitHub-Api-Version", "2022-11-28")
            }
        }
    }

    init {
        launchThread {
            checkForUpdate()
        }
    }

    private suspend fun checkForUpdate() {
        mutex.withLock {
            try {
                latestRelease = getUpdateFromGitHub()
            } catch (exception: Exception) {
                // logger.warn("error updating", exception)
            }
        }
    }

    private suspend fun getUpdateFromGitHub(): GithubRelease? {
        if (System.getenv("EMULATOR_IGNORE_UPDATES").toBoolean()) {
            logger.debug("ignoring updates based on env var")
            return null
        }
        val packageVersion = System.getProperty("jpackage.app-version", "1.0.0")

        logger.info("checking for latest update currentVersion=$packageVersion")

        if (packageVersion == null) {
            logger.debug("missing package version")
            updateState.value = UpdateState.None
            return null
        }

        val githubReleases: List<GithubRelease> = client.get("$BASE_URL/releases").body()

        val latestRelease = githubReleases.maxByOrNull {
            it.publishedAt
        }

        if (latestRelease == null) {
            logger.debug("no latest release found")
            updateState.value = UpdateState.None
            return null
        }

        val currentSemVer: SemVer = SemVer.parse(packageVersion.replace("v", ""))
        val latestSemVer: SemVer = SemVer.parse(latestRelease.tagName.replace("v", ""))

        if (currentSemVer >= latestSemVer) {
            logger.debug("same version or higher, skipping")
            updateState.value = UpdateState.None
            return null
        }

        updateState.value = UpdateState.Available

        return latestRelease
    }

    suspend fun update(
        githubRelease: GithubRelease
    ) {
        val asset = githubRelease.assets.getByFileEnding()

        if (asset == null) {
            return
        }

        val file = File(
            buildString {
                append(System.getProperty("user.home"))
                append(File.separator)
                append("monta")
                append(File.separator)
                append(asset.name)
            }
        )

        if (shouldDownloadFile(asset, file)) {
            updateState.value = UpdateState.Downloading
            try {
                downloadFile(asset, file)
            } catch (exception: Exception) {
                logger.warn("failed to download update", exception)
                updateState.value = UpdateState.None
            }
        }

        withContext(Dispatchers.IO) {
            openInstaller(file)
        }

        updateState.value = UpdateState.None

        return
    }

    private fun shouldDownloadFile(
        asset: GithubAsset,
        file: File
    ): Boolean {
        return if (file.exists()) {
            Files.size(file.toPath()) != asset.size.toLong()
        } else {
            true
        }
    }

    private suspend fun downloadFile(
        asset: GithubAsset,
        file: File
    ) {
        val response: HttpResponse = downloadClient.get("$BASE_URL/releases/assets/${asset.id}") {
            onDownload { bytesSentTotal, contentLength ->
                val progress = contentLength?.let { contentLength ->
                    bytesSentTotal.toFloat() / contentLength.toFloat()
                } ?: 0F
                logger.trace("Downloading ($bytesSentTotal / $contentLength) $progress")
                downloadProgress.value = progress
            }
        }

        val body: ByteArray = response.body()

        file.writeBytes(body)
    }

    private fun openInstaller(file: File) {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file)
                exitProcess(0)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun List<GithubAsset>.getByFileEnding(): GithubAsset? {
        for (asset in this) {
            for (fileFormat in currentOS.fileFormat) {
                if (asset.name.endsWith(fileFormat)) {
                    return asset
                }
            }
        }
        return null
    }

    fun clearUpdate() {
        updateState.value = UpdateState.None
    }

    private val currentOS: OS by lazy {
        val os = System.getProperty("os.name")
        when {
            os.equals("Mac OS X", ignoreCase = true) -> OS.MacOS
            os.startsWith("Win", ignoreCase = true) -> OS.Windows
            os.startsWith("Linux", ignoreCase = true) -> OS.Linux
            else -> error("Unknown OS name: $os")
        }
    }
}
