package com.easyfetch.app.data.network

import com.easyfetch.app.data.model.CobaltRequest
import com.easyfetch.app.data.model.CobaltResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ExtractionApiService(
    private val baseUrl: String,
    private val httpClient: OkHttpClient
) {

    suspend fun extract(request: CobaltRequest): CobaltResponse = withContext(Dispatchers.IO) {
        val jsonBody = NetworkModule.json.encodeToString(request)
        val mediaType = "application/json".toMediaType()
        val httpRequest = Request.Builder()
            .url(baseUrl)
            .header("Accept", "application/json")
            .post(jsonBody.toRequestBody(mediaType))
            .build()

        httpClient.newCall(httpRequest).execute().use { response ->
            val responseBody = response.body?.string()
            if (responseBody.isNullOrBlank()) {
                throw IOException("Empty response from extraction service.")
            }
            NetworkModule.json.decodeFromString(CobaltResponse.serializer(), responseBody)
        }
    }
}
