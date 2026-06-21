package com.easyfetch.app.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyfetch.app.data.model.DownloadedVideo
import com.easyfetch.app.data.repository.MediaStorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LibraryViewModel(private val repository: MediaStorageRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val videos = repository.listDownloadedVideos()
                _uiState.update { it.copy(isLoading = false, videos = videos) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Couldn't load downloaded videos.") }
            }
        }
    }

    fun deleteVideo(video: DownloadedVideo) {
        viewModelScope.launch {
            val deleted = repository.deleteVideo(video)
            if (deleted) {
                _uiState.update { state -> state.copy(videos = state.videos.filterNot { it.id == video.id }) }
            } else {
                _uiState.update { it.copy(errorMessage = "Couldn't delete this video.") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
