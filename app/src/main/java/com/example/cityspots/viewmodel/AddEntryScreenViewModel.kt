package com.example.cityspots.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.example.cityspots.model.Entry
import com.example.cityspots.model.GeoLocation
import com.example.cityspots.model.RankingList


class AddEntryScreenViewModel(private val userViewModel: UserViewModel) : ViewModel() {
    private val _clonedRankings = MutableLiveData<RankingList>(userViewModel.getRankingsClone())
    val clonedRankings: LiveData<RankingList> = _clonedRankings
    val newEntryId = userViewModel.getNewEntryId()
    init{
        insertEntryInClone(clonedRankings.value!!.length(),Entry(
            id = newEntryId,
            title =  "New Entry",
            pictures = listOf(), // Add picture URLs if necessary
            review = "",
            tags = listOf(), // Add picture URLs if necessary
            geoLocation = GeoLocation(0.0, 0.0)
            )
        )
    }

    // Transform clonedRankings to update newEntryRanking whenever clonedRankings changes
    val newEntryRanking: LiveData<Int> = _clonedRankings.map { rankings ->
        rankings.getRankById(newEntryId) ?: -1 // Use -1 or any other default value to indicate "not found"
    }

    fun insertEntryInClone(rank: Int, entry: Entry) {
        _clonedRankings.value?.let { clonedList ->
            if (rank in 0..clonedList.length()) {
                clonedList.insert(rank, entry)
                _clonedRankings.postValue(clonedList) // This triggers the LiveData to update
            }
        }
    }

    fun commitClonedRankingsToUser() {
        _clonedRankings.value?.let {
            userViewModel.updateUserRankings(it)
        }
    }
}

class AddEntryScreenViewModelFactory(private val userViewModel: UserViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEntryScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEntryScreenViewModel(userViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}