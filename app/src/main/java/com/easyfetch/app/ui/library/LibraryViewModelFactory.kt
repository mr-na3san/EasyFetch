package com.easyfetch.app.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.easyfetch.app.data.repository.MediaStorageRepository

class LibraryViewModelFactory(
    private val repository: MediaStorageRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryViewModel(repository) as T
    }
}
