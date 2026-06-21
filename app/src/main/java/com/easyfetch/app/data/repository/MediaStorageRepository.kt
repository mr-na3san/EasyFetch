package com.easyfetch.app.data.repository

import com.easyfetch.app.data.model.DownloadedVideo

sealed class SaveResult {
    data class Success(val video: DownloadedVideo) : SaveResult()
    data class Error(val message: String) : SaveResult()
}

interface MediaStorageRepository {
    suspend fun saveVideoToGallery(sourceUrl: String, fileName: String): SaveResult
    suspend fun listDownloadedVideos(): List<DownloadedVideo>
    suspend fun deleteVideo(video: DownloadedVideo): Boolean
}
