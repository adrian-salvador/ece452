package com.group22.cityspots.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group22.cityspots.model.Entry
import com.group22.cityspots.respository.Firestore
import kotlinx.coroutines.launch

class RankingScreenViewModel(private val userId: String) : ViewModel() {
    private var originalEntries = listOf<Entry>()
    private val _entriesLiveData = MutableLiveData<List<Entry>>()
    val entriesLiveData: LiveData<List<Entry>> = _entriesLiveData
    val tags = MutableLiveData<List<String>>()

    init {
        loadEntries()
    }

    private fun loadEntries() {
        viewModelScope.launch {
            val entries = Firestore().getEntriesByUserId(userId)
            originalEntries = entries
            _entriesLiveData.postValue(entries)
        }
    }

    fun addTag(tag: String) {
        val currentTags = tags.value.orEmpty()
        if (tag !in currentTags) {
            val updatedTags = currentTags + tag
            tags.postValue(updatedTags)
            applyTagFilter(updatedTags)
        }
    }

    fun removeTag(tag: String) {
        val currentTags = tags.value.orEmpty()
        if (tag in currentTags) {
            val updatedTags = currentTags.toMutableList().apply { remove(tag) }
            tags.postValue(updatedTags)
            applyTagFilter(updatedTags)
        }
    }


    fun applyTagFilter(tagsToFilter: List<String>) {
        if (tagsToFilter.isEmpty()) {
            _entriesLiveData.postValue(originalEntries)
            println(tagsToFilter)
        } else {
            val filteredEntries = originalEntries.filter { entry ->
                entry.tags.any { tag -> tag in tagsToFilter }
            }
            println(tagsToFilter)
            _entriesLiveData.postValue(filteredEntries)
        }
    }

}

class RankingScreenViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankingScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RankingScreenViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

