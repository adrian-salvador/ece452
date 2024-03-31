package com.group22.cityspots.viewmodel

import android.annotation.SuppressLint
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
import okhttp3.internal.wait

class AddEntryViewModel(private val userId: String) : ViewModel() {
    val ratingLiveData = MutableLiveData<Double>()
    val entriesLiveData = MutableLiveData<List<Entry>>()
    val imageUrls = MutableLiveData<List<String>>(listOf())
    val tags = MutableLiveData<List<String>>()

    init {
        ratingLiveData.value = 0.0
        loadEntries()
    }

    fun updateRating(newRating: Double) {
        ratingLiveData.value = newRating
    }

    fun addTag(tag: String) {
        val currentTags = tags.value.orEmpty()
        if (tag !in currentTags) {
            val updatedTags = currentTags + tag
            tags.postValue(updatedTags)
        }
    }

    fun removeTag(tag: String) {
        val currentTags = tags.value.orEmpty()
        if (tag in currentTags) {
            val updatedTags = currentTags.toMutableList().apply { remove(tag) }
            tags.postValue(updatedTags)
        }
    }

    private fun loadEntries() {
        viewModelScope.launch {
            val entries = Firestore().getEntriesByUserId(userId).asReversed()
            entriesLiveData.postValue(entries)
        }
    }
    fun getAdjacentEntriesIndicesForRating(rating: Double): Pair<Int?, Int?> {
        val entries = entriesLiveData.value ?: return Pair(null, null)
        var previousEntryIndex: Int? = null
        var nextEntryIndex: Int? = null

        for ((index, entry) in entries.withIndex()) {
            if (entry.rating <= rating ) {
                previousEntryIndex = index
            }
            if (entry.rating > rating) {
                nextEntryIndex = index
                break
            }
        }

        return Pair(previousEntryIndex, nextEntryIndex)
    }

    fun uploadImage(context: Context, imageUri: Uri, userId: String) {
        viewModelScope.launch {
            uploadImageAndGetUrl(imageUri, context, userId, imageUrls)
        }
    }
    @SuppressLint("Recycle")
    suspend fun uploadImageAndGetUrl(imageUri: Uri, context: Context, userId: String, imageUrls: MutableLiveData<List<String>>) {
        val imageData = context.contentResolver.openInputStream(imageUri)?.readBytes()
            ?: throw IllegalArgumentException("Unable to convert Uri to bytes")

        val imageUrl = Firestore().uploadPicture(imageData, userId, context)

        imageUrl?.let { nonNullImageUrl ->
            val currentList = imageUrls.value ?: emptyList()
            val updatedList = currentList + nonNullImageUrl
            imageUrls.postValue(updatedList)
        }
    }

    fun deleteImage(imagePath: String, context: Context) {
        viewModelScope.launch {
            val success = Firestore().deletePicture(imagePath, context)

            if (success) {
                val currentList = imageUrls.value ?: listOf()
                val updatedList = currentList.toMutableList().apply {
                    remove(imagePath)
                }
                imageUrls.postValue(updatedList)
            }
        }
    }

    fun createEntry( entryDetails: Entry, context: Context) {
        viewModelScope.launch {
            val newEntry = Entry(
                entryId = "",
                title = entryDetails.title,
                pictures = imageUrls.value,
                review = entryDetails.review,
                tripId = entryDetails.tripId,
                tags = entryDetails.tags,
                placeId = entryDetails.placeId,
                address = entryDetails.address,
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