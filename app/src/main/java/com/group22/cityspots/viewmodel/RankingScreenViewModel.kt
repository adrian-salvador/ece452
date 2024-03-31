package com.group22.cityspots.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import coil.request.Tags
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.group22.cityspots.model.Entry
import com.group22.cityspots.model.Trip
import com.group22.cityspots.respository.Firestore
import kotlinx.coroutines.launch

class RankingScreenViewModel(private val userId: String) : ViewModel() {
    private var originalEntries = listOf<Entry>()
    private val _entriesLiveData = MutableLiveData<List<Entry>>()
    private var originalTrips = listOf<Trip>()
    private val _tripsLiveData = MutableLiveData<List<Trip>>()
    val tripsLiveData: LiveData<List<Trip>> = _tripsLiveData
    val entriesLiveData: MutableLiveData<List<Entry>> = _entriesLiveData
    val tags = MutableLiveData<List<String>>(listOf())
    val selectedTrip: MutableLiveData<Trip?> = MutableLiveData<Trip?>()

    init {
        loadEntries()
        loadTrips()
    }

    private fun loadEntries() {
        Firebase.firestore.collection("entries").whereEqualTo("userId", userId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    // Handle error
                    return@addSnapshotListener
                }
                val entries = value?.toObjects(Entry::class.java)
                originalEntries = entries ?: listOf()
                updateFilters(tags.value,selectedTrip.value)
            }
    }

    private fun loadTrips() {
        viewModelScope.launch {
            val trips = Firestore().getTripsByUserId(userId)
            originalTrips = trips
            _tripsLiveData.postValue(trips)
        }
    }

    fun addTag(tag: String) {
        val currentTags = tags.value.orEmpty()
        if (tag !in currentTags) {
            val updatedTags = currentTags + tag
            tags.postValue(updatedTags)
            updateFilters(updatedTags, selectedTrip.value)
        }
    }

    fun removeTag(tag: String) {
        val currentTags = tags.value.orEmpty()
        if (tag in currentTags) {
            val updatedTags = currentTags.toMutableList().apply { remove(tag) }
            tags.postValue(updatedTags)
            updateFilters(updatedTags, selectedTrip.value)
        }
    }

    fun setSelectedTrip(trip: Trip?) {
        selectedTrip.postValue(trip)
        updateFilters(tags.value, trip)
    }

    private fun updateFilters(tags: List<String>?, trip: Trip?) {
        // Apply tag filter
        val tagFilteredEntries = if (tags.isNullOrEmpty()) {
            originalEntries
        } else {
            originalEntries.filter { entry ->
                entry.tags.any { tag -> tag in tags.orEmpty() }
            }
        }

        // Apply trip filter

        val finalFilteredEntries = if (trip != null){
            trip.let { trip ->
                tagFilteredEntries.filter { entry ->
                    entry.tripId == trip.tripId
                }
            }
        } else tagFilteredEntries

        // Post the final filtered list
        _entriesLiveData.postValue(finalFilteredEntries)
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

