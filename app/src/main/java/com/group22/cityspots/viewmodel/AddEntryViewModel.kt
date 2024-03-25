package com.group22.cityspots.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group22.cityspots.model.Entry
import com.group22.cityspots.respository.Firestore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class AddEntryViewModel(private val userId: String) : ViewModel() {
    val ratingLiveData = MutableLiveData<Double>()
    val entriesLiveData = MutableLiveData<List<Entry>>()

    init {
        ratingLiveData.value = 0.0
        loadEntries()
    }

    fun updateRating(newRating: Double) {
        ratingLiveData.value = newRating
    }

    private fun loadEntries() {
        viewModelScope.launch {
            val entries = Firestore().getEntriesByUserId(userId)
            entriesLiveData.postValue(entries)
        }
    }
    fun getAdjacentEntriesForRating(rating: Double): Pair<Entry?, Entry?> {
        val entries = entriesLiveData.value ?: return Pair(null, null)
        var previousEntry: Entry? = null
        var nextEntry: Entry? = null

        for (entry in entries) {
            if (entry.rating > rating) {
                previousEntry = entry
            } else if (entry.rating < rating) {
                nextEntry = entry
                break
            }
        }

        return Pair(previousEntry, nextEntry)
    }
    fun uploadImagesAndCreateEntry(images: List<Uri>, entryDetails: Entry, context: Context) {
        viewModelScope.launch {
            val imageUrls = images.map { uri ->
                async {
                    val imageData = uri.toBytes(context)
                    Firestore().uploadPicture(imageData, userId, context)
                }
            }.awaitAll()

            val newEntry = Entry(
                entryId = "",
                title = entryDetails.title,
                pictures = imageUrls.filterNotNull(),
                review = entryDetails.review,
                tags = entryDetails.tags,
                geoLocation = entryDetails.geoLocation,
                rating = ratingLiveData.value ?: 0.0,
                userId = userId
            )

            Firestore().saveEntry(newEntry, context)
        }
    }

    private fun Uri.toBytes(context: Context): ByteArray {
        val inputStream = context.contentResolver.openInputStream(this)
        return inputStream!!.readBytes()
    }

}

class AddEntryViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEntryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddEntryViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}