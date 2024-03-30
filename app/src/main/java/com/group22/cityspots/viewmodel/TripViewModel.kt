package com.group22.cityspots.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group22.cityspots.model.Trip
import com.group22.cityspots.respository.Firestore
import kotlinx.coroutines.launch

class TripViewModel(private val userId: String) : ViewModel() {
    private var originalTrips = listOf<Trip>()
    private val _tripsLiveData = MutableLiveData<List<Trip>>()
    val tripsLiveData: LiveData<List<Trip>> = _tripsLiveData

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            val trips = Firestore().getTripsByUserId(userId)
            originalTrips = trips
            _tripsLiveData.postValue(trips)
        }
    }

    fun refreshTrips() {
        loadTrips()
    }
}

class TripViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

