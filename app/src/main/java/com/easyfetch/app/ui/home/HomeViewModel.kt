package com.easyfetch.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyfetch.app.data.repository.ExtractionRepository
import com.easyfetch.app.data.repository.ExtractionResult
import com.easyfetch.app.data.repository.MediaStorageRepository
import com.easyfetch.app.data.repository.SaveResult
import com.easyfetch.app.domain.UrlValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val extractionRepository: ExtractionRepository,
    private val mediaStorageRepository: MediaStorageRepository,
    private val onVideoSaved: () -> Unit
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun onUrlChanged(newValue: String) {
        _uiState.update { it.copy(urlInput = newValue, errorMessage = null) }
    }

    fun onSharedTextReceived(sharedText: String) {
        val extracted = UrlValidator.extractFirstUrl(sharedText) ?: sharedText.trim()
        _uiState.update { it.copy(urlInput = extracted) }
        fetchVideo()
    }

    fun fetchVideo() {
        val url = _uiState.value.urlInput.trim()
        if (url.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter a video link first.") }
            return
        }
        _uiState.update {
            it.copy(isLoading = true, errorMessage = null, successMessage = null, video = null)
        }
        viewModelScope.launch {
            when (val result = extractionRepository.extractVideo(url)) {
                is ExtractionResult.Success -> _uiState.update {
                    it.copy(isLoading = false, video = result.video, successMessage = "Video ready to download.")
                }
                is ExtractionResult.Error -> _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun downloadVideo() {
        val video = _uiState.value.video ?: return
        _uiState.update { it.copy(isDownloading = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            when (val result = mediaStorageRepository.saveVideoToGallery(video.downloadUrl, video.suggestedFileName)) {
                is SaveResult.Success -> {
                    _uiState.update {
                        it.copy(isDownloading = false, successMessage = "Saved to Movies/EasyFetch.")
                    }
                    onVideoSaved()
                }
                is SaveResult.Error -> _uiState.update {
                    it.copy(isDownloading = false, errorMessage = result.message)
                }
            }
        }
    }

    fun onPermissionDenied() {
        _uiState.update {
            it.copy(errorMessage = "Storage permission is required to save videos on this Android version.")
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
