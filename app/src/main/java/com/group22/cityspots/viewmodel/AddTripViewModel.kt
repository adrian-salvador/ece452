package com.group22.cityspots.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group22.cityspots.model.Trip
import com.group22.cityspots.respository.Firestore
import kotlinx.coroutines.launch

class AddTripViewModel(private val userId: String) : ViewModel() {
    fun createTrip(tripDetails: Trip, context: Context) {
        viewModelScope.launch {
            val newTrip = Trip(
                tripId = "",
                title = tripDetails.title,
                userId = userId
            )
            Firestore().saveTrip(newTrip, context)
        }
    }
}

class AddTripViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddTripViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}