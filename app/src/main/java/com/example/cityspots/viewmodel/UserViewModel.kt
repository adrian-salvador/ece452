package com.example.cityspots.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cityspots.model.Entry
import com.example.cityspots.model.GeoLocation
import com.example.cityspots.model.User

class UserViewModel() : ViewModel() {

    val userLiveData = MutableLiveData<User?>()
    private val userId = "test"

    init {
        fetchUserData("user1") // Assuming a default or logged-in user ID for simplicity
    }

    fun loginUser(userId: String) {
        // Simulate fetching user data for the logged-in user
        fetchUserData(userId)
    }

    private fun fetchUserData(userId: String) {
        // Mocking user data with initial entries
        val mockUser = User(
            id = userId,
            name = "Alice",
            entries = mutableListOf(
                // Your initial mock entries
            )
        )
        fetchUserEntries(mockUser) // Fetch entries and add them to the mockUser
        userLiveData.postValue(mockUser)
    }
    private fun fetchUserEntries(user: User) {
        // Mocking entries data and adding them to the user's entries list
        val mockEntries = mutableListOf(
            Entry(
                id = 1,
                content = "Central Park Visit",
                pictures = listOf("https://example.com/pic1.jpg", "https://example.com/pic2.jpg"),
                ranking = 5,
                review = "A peaceful escape from the city buzz. Loved the greenery and lakes!",
                tags = listOf("park", "nature", "family-friendly"),
                geoLocation = GeoLocation(40.785091, -73.968285) // Central Park, NY
            ),
            Entry(
                id = 2,
                content = "Golden Gate Bridge Sightseeing",
                pictures = listOf("https://example.com/pic3.jpg", "https://example.com/pic4.jpg"),
                ranking = 4,
                review = "Iconic structure with breathtaking views, but quite windy.",
                tags = listOf("bridge", "landmark", "scenic"),
                geoLocation = GeoLocation(37.8199, -122.4783) // Golden Gate Bridge, SF
            ),
            Entry(
                id = 3,
                content = "Louvre Museum Tour",
                pictures = listOf("https://example.com/pic5.jpg", "https://example.com/pic6.jpg"),
                ranking = 5,
                review = "The art collection is impressive. The Mona Lisa is a must-see!",
                tags = listOf("museum", "art", "culture"),
                geoLocation = GeoLocation(48.8606, 2.3376) // Louvre Museum, Paris
            ),
            Entry(
                id = 4,
                content = "Mount Fuji Hike",
                pictures = listOf("https://example.com/pic7.jpg", "https://example.com/pic8.jpg"),
                ranking = 5,
                review = "Challenging hike but the view from the top is worth every step.",
                tags = listOf("mountain", "hiking", "nature"),
                geoLocation = GeoLocation(35.3606, 138.7274) // Mount Fuji, Japan
            ),
            Entry(
                id = 5,
                content = "Venice Canal Tour",
                pictures = listOf("https://example.com/pic9.jpg", "https://example.com/pic10.jpg"),
                ranking = 4,
                review = "Unique city with stunning architecture and history. The gondola ride was magical.",
                tags = listOf("canals", "boating", "historic"),
                geoLocation = GeoLocation(45.4408, 12.3155) // Venice, Italy
            )
            // Add more entries as needed
        )
        user.entries.addAll(mockEntries)
    }

    fun addEntry(entry: Entry) {
        val currentUser = userLiveData.value
        currentUser?.entries?.add(entry)
        userLiveData.postValue(currentUser) // Update LiveData to notify observers
    }

    fun updateEntry(entry: Entry) {
        val currentUser = userLiveData.value
        currentUser?.entries?.find { it.id == entry.id }?.let {
            val index = currentUser.entries.indexOf(it)
            currentUser.entries[index] = entry
            userLiveData.postValue(currentUser) // Update LiveData to notify observers
        }
    }
}
