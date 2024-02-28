package com.example.cityspots.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import com.example.cityspots.model.Entry
import com.example.cityspots.model.GeoLocation
import kotlin.random.Random

class EntryViewModel : ViewModel() {
    val currentEntry = MutableLiveData<Entry?>()

    fun createEntry(content: String, pictures: List<String>, ranking: Int, review: String, tags: List<String>, geoLocation: GeoLocation) {
        val newEntry = Entry(
            id = generateNewId(), // Implement this method based on your ID generation strategy
            content = content,
            pictures = pictures,
            ranking = ranking,
            review = review,
            tags = tags,
            geoLocation = geoLocation
        )
        currentEntry.value = newEntry
        // Add logic to persist the new entry if necessary
    }

    fun updateEntry(entry: Entry) {
        // Add logic to update the entry
        currentEntry.value = entry
        // Update the entry in the data source
    }

    private fun generateNewId(): Int {
        // Generate a random ID. Ensure this method generates a unique ID in your entries list.
        return Random.nextInt(1, Int.MAX_VALUE)
    }
}
