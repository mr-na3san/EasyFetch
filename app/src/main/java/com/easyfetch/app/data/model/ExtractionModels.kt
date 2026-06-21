package com.easyfetch.app.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CobaltRequest(
    val url: String,
    val downloadMode: String = "auto",
    val filenameStyle: String = "basic",
    val alwaysProxy: Boolean = true
)

@Serializable
data class CobaltResponse(
    val status: String,
    val url: String? = null,
    val filename: String? = null,
    val audio: String? = null,
    val audioFilename: String? = null,
    val picker: List<CobaltPickerItem>? = null,
    val error: CobaltError? = null
)

@Serializable
data class CobaltPickerItem(
    val type: String,
    val url: String,
    val thumb: String? = null
)

@Serializable
data class CobaltError(
    val code: String,
    val context: CobaltErrorContext? = null
)

@Serializable
data class CobaltErrorContext(
    val service: String? = null,
    val limit: Int? = null
)

data class VideoResult(
    val sourceUrl: String,
    val platform: Platform,
    val title: String,
    val thumbnailUrl: String?,
    val downloadUrl: String,
    val suggestedFileName: String
)
