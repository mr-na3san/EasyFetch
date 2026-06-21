package com.easyfetch.app.ui.library

import com.easyfetch.app.data.model.DownloadedVideo

data class LibraryUiState(
    val isLoading: Boolean = true,
    val videos: List<DownloadedVideo> = emptyList(),
    val errorMessage: String? = null
)
