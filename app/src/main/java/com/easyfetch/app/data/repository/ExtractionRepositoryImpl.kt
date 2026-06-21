package com.easyfetch.app.data.repository

import com.easyfetch.app.data.model.CobaltRequest
import com.easyfetch.app.data.model.VideoResult
import com.easyfetch.app.data.network.ExtractionApiService
import com.easyfetch.app.data.network.OpenGraphFetcher
import com.easyfetch.app.domain.UrlValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.UUID

class ExtractionRepositoryImpl(
    private val api: ExtractionApiService,
    private val openGraphFetcher: OpenGraphFetcher
) : ExtractionRepository {

    override suspend fun extractVideo(url: String): ExtractionResult = withContext(Dispatchers.IO) {
        val platform = UrlValidator.detectPlatform(url)
            ?: return@withContext ExtractionResult.Error("This link isn't from a supported platform.")

        try {
            coroutineScope {
                val metadataDeferred = async { openGraphFetcher.fetchMetadata(url) }
                val response = api.extract(CobaltRequest(url = url))
                val metadata = metadataDeferred.await()

                when (response.status) {
                    "tunnel", "redirect" -> {
                        val downloadUrl = response.url
                        if (downloadUrl.isNullOrBlank()) {
                            ExtractionResult.Error("This video is unavailable or unsupported.")
                        } else {
                            ExtractionResult.Success(
                                VideoResult(
                                    sourceUrl = url,
                                    platform = platform,
                                    title = metadata?.title?.takeIf { it.isNotBlank() }
                                        ?: "${platform.displayName} video",
                                    thumbnailUrl = metadata?.imageUrl,
                                    downloadUrl = downloadUrl,
                                    suggestedFileName = response.filename?.takeIf { it.isNotBlank() }
                                        ?: defaultFileName()
                                )
                            )
                        }
                    }

                    "picker" -> {
                        val firstItem = response.picker?.firstOrNull { it.type == "video" }
                            ?: response.picker?.firstOrNull()
                        if (firstItem == null) {
                            ExtractionResult.Error("No downloadable media was found in this post.")
                        } else {
                            ExtractionResult.Success(
                                VideoResult(
                                    sourceUrl = url,
                                    platform = platform,
                                    title = metadata?.title?.takeIf { it.isNotBlank() }
                                        ?: "${platform.displayName} video",
                                    thumbnailUrl = firstItem.thumb ?: metadata?.imageUrl,
                                    downloadUrl = firstItem.url,
                                    suggestedFileName = defaultFileName()
                                )
                            )
                        }
                    }

                    "local-processing" -> ExtractionResult.Error(
                        "This video requires advanced processing that isn't supported yet."
                    )

                    "error" -> ExtractionResult.Error(
                        response.error?.code?.let { describeErrorCode(it) }
                            ?: "This video is unavailable or unsupported."
                    )

                    else -> ExtractionResult.Error("Unexpected response from the extraction service.")
                }
            }
        } catch (e: SocketTimeoutException) {
            ExtractionResult.Error("The request timed out. Please check your connection and try again.")
        } catch (e: UnknownHostException) {
            ExtractionResult.Error("No internet connection available.")
        } catch (e: SerializationException) {
            ExtractionResult.Error("The extraction service returned an unexpected response.")
        } catch (e: IOException) {
            ExtractionResult.Error("Network error occurred. Please try again.")
        } catch (e: Exception) {
            ExtractionResult.Error("Something went wrong while processing this video.")
        }
    }

    private fun defaultFileName(): String = "easyfetch_${UUID.randomUUID().toString().take(8)}.mp4"

    private fun describeErrorCode(code: String): String = when {
        code.contains("link.invalid") -> "This link doesn't look valid."
        code.contains("content.too_long") -> "This video is too long to download."
        code.contains("content.private") || code.contains("content.unavailable") ->
            "This content is private or unavailable."
        code.contains("rate_limit") -> "Too many requests right now. Please try again shortly."
        else -> "This video is unavailable or unsupported ($code)."
    }
}
