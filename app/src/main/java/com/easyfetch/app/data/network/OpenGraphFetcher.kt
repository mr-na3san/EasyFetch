package com.easyfetch.app.data.network

import com.easyfetch.app.data.model.OpenGraphMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class OpenGraphFetcher(private val httpClient: OkHttpClient) {

    suspend fun fetchMetadata(pageUrl: String): OpenGraphMetadata? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(pageUrl)
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/124.0 Mobile Safari/537.36"
                )
                .build()

            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null
                val html = response.body?.string() ?: return@withContext null
                val document = Jsoup.parse(html, pageUrl)

                val title = document.select("meta[property=og:title]").attr("content")
                    .ifBlank { document.title() }
                val image = document.select("meta[property=og:image]").attr("content")

                OpenGraphMetadata(
                    title = title.ifBlank { null },
                    imageUrl = image.ifBlank { null }
                )
            }
        } catch (e: Exception) {
            null
        }
    }
}
