package com.group22.cityspots.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group22.cityspots.model.Entry
import com.group22.cityspots.model.User
import com.group22.cityspots.respository.Firestore
import kotlinx.coroutines.launch

class EntryViewModel(private val user: User) : ViewModel() {
    val entriesLiveData = MutableLiveData<List<Entry>>()
    val cityEntriesLiveData = MutableLiveData<List<Entry>>()
    init {
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            val entries = Firestore().getEntriesByUserId(user.userId)
            println(entries)
            entriesLiveData.postValue(entries)
        }
    }

     fun loadCityEntries(address: String) {
        viewModelScope.launch {
            var entries = Firestore().getEntriesByAddress(address)
            println(entries)

            if (entries.isEmpty()) entries = emptyList<Entry>()
            cityEntriesLiveData.postValue(entries)
        }
    }
}

class EntryViewModelFactory(private val user: User) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EntryViewModel(user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

