package com.easyfetch.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.easyfetch.app.data.repository.ExtractionRepository
import com.easyfetch.app.data.repository.MediaStorageRepository

class HomeViewModelFactory(
    private val extractionRepository: ExtractionRepository,
    private val mediaStorageRepository: MediaStorageRepository,
    private val onVideoSaved: () -> Unit
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(extractionRepository, mediaStorageRepository, onVideoSaved) as T
    }
}
