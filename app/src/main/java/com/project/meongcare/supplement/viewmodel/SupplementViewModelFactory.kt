package com.project.meongcare.supplement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.meongcare.supplement.model.data.repository.SupplementRepository

class SupplementViewModelFactory(private val repository: SupplementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SupplementViewModel::class.java)) {
            return SupplementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
