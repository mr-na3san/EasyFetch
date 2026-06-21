package com.easyfetch.app.data.repository

import com.easyfetch.app.data.model.VideoResult

sealed class ExtractionResult {
    data class Success(val video: VideoResult) : ExtractionResult()
    data class Error(val message: String) : ExtractionResult()
}

interface ExtractionRepository {
    suspend fun extractVideo(url: String): ExtractionResult
}
