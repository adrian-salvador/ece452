package com.group22.cityspots.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group22.cityspots.model.Entry
import com.group22.cityspots.respository.Firestore
import kotlinx.coroutines.launch

class EntryViewModel(private val userId: String) : ViewModel() {
    val entriesLiveData = MutableLiveData<List<Entry>>()
    init {
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            val entries = Firestore().getEntriesByUserId(userId)
            println(entries)
            entriesLiveData.postValue(entries)
        }
    }
}

class EntryViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EntryViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

