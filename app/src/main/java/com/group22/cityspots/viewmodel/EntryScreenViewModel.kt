package com.group22.cityspots.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group22.cityspots.model.Entry
import com.group22.cityspots.respository.Firestore
import kotlinx.coroutines.launch

class EntryScreenViewModel(private val entryId: String) : ViewModel() { // Extend ViewModel
    val entry = MutableLiveData<Entry?>()

    init {
        loadEntry()
    }

    private fun loadEntry() {
        viewModelScope.launch {
            val foundEntry = Firestore().getEntryByEntryId(entryId)
            entry.postValue(foundEntry)
        }
    }
}

class EntryScreenViewModelFactory(private val entryId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EntryScreenViewModel(entryId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
