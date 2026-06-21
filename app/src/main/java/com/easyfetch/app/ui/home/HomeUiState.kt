package com.easyfetch.app.ui.home

import com.easyfetch.app.data.model.VideoResult

data class HomeUiState(
    val urlInput: String = "",
    val isLoading: Boolean = false,
    val isDownloading: Boolean = false,
    val video: VideoResult? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
