package com.easyfetch.app.data.model

import android.net.Uri

data class DownloadedVideo(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val dateAddedSeconds: Long,
    val sizeBytes: Long
)
