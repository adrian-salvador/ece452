package com.group22.cityspots.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group22.cityspots.model.User
import com.group22.cityspots.respository.Firestore
import kotlinx.coroutines.launch

class UserViewModel(private val defaultUser: User) : ViewModel() {
    private val firestore = Firestore()
    val userLiveData = MutableLiveData<User>()
    val citiesLiveData = MutableLiveData<List<String>>()

    init {
        userLiveData.value = defaultUser
        getUserCities()
    }

    fun saveUser(user: User, context: Context) {
        userLiveData.value = user
        viewModelScope.launch {
            firestore.saveUser(user, context)
        }
    }

    private fun getUserCities() {
        val currentUser = userLiveData.value!!

        viewModelScope.launch {
            currentUser.cities = firestore.getCitiesByUserId(userId = userLiveData.value?.userId!!).toMutableList()
            citiesLiveData.postValue(currentUser.cities)
        }
    }

    fun addUserCity(city: String, context: Context) {
        userLiveData.value!!.cities.add(city)

        viewModelScope.launch {
            firestore.updateUser(userLiveData.value!!, context)
            citiesLiveData.postValue(userLiveData.value!!.cities)
        }
    }

    fun removeUserCity(city: String, context: Context) {
        userLiveData.value!!.cities.remove(city)

        viewModelScope.launch {
            firestore.updateUser(userLiveData.value!!, context)
            citiesLiveData.postValue(userLiveData.value!!.cities)
        }
    }
}

class UserViewModelFactory(private val defaultUser: User) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(defaultUser) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
//class UserViewModel(user: User) : ViewModel() {
//    val userLiveData = MutableLiveData<User>(user)
//    val userId = MutableLiveData<Int>(0)
//
//    init {
//        fetchUserEntries(user)
//        fetchUserFriends(user)
//    }
//
//    fun loginUser(userId: Int) {
//        this.userId.value = userId
//        // Simulate fetching user data for the logged-in user
//    }
//
//    private fun fetchUserEntries(user: User) {
//        // Mocking entries data and adding them to the user's entries list
//        val mockEntries = mutableListOf(
//            // Images used are copyright free
//            Entry(
//                id = 1,
//                title = "Central Park Visit",
//                pictures = listOf("https://images.pexels.com/photos/327502/pexels-photo-327502.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
//                review = "A peaceful escape from the city buzz. Loved the greenery and lakes!",
//                tags = listOf("park", "nature", "family-friendly"),
//                geoLocation = GeoLocation(40.785091, -73.968285) // Central Park, NY
//            ),
//            Entry(
//                id = 2,
//                title = "Golden Gate Bridge Sightseeing",
//                pictures = listOf("https://images.pexels.com/photos/208745/pexels-photo-208745.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
//                review = "Iconic structure with breathtaking views, but quite windy.",
//                tags = listOf("bridge", "landmark", "scenic"),
//                geoLocation = GeoLocation(37.8199, -122.4783) // Golden Gate Bridge, SF
//            ),
//            Entry(
//                id = 3,
//                title = "Louvre Museum Tour",
//                pictures = listOf("https://images.pexels.com/photos/2363/france-landmark-lights-night.jpg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
//                review = "The art collection is impressive. The Mona Lisa is a must-see!",
//                tags = listOf("museum", "art", "culture"),
//                geoLocation = GeoLocation(48.8606, 2.3376) // Louvre Museum, Paris
//            ),
//            Entry(
//                id = 4,
//                title = "Mount Fuji Hike",
//                pictures = listOf("https://images.pexels.com/photos/3408353/pexels-photo-3408353.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
//                review = "Challenging hike but the view from the top is worth every step.",
//                tags = listOf("mountain", "hiking", "nature"),
//                geoLocation = GeoLocation(35.3606, 138.7274) // Mount Fuji, Japan
//            ),
//            Entry(
//                id = 5,
//                title = "Venice Canal Tour",
//                pictures = listOf("https://images.pexels.com/photos/15857929/pexels-photo-15857929/free-photo-of-bridge-over-canal-in-venice.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"),
//                review = "Unique city with stunning architecture and history. The gondola ride was magical.",
//                tags = listOf("canals", "boating", "historic"),
//                geoLocation = GeoLocation(45.4408, 12.3155) // Venice, Italy
//            )
//        )
//        user.rankings = RankingList(mockEntries)
//        user.total_entry_count+=user.rankings.length()
//    }
//
//    private fun fetchUserFriends(user: User) {
//        // Mocking entries data and adding them to the user's entries list
//        val mockFriends = mutableListOf(
//            Friend(
//                id = 1,
//                name = "Jennifer",
//                username = "jennifer123"
//            ),
//            Friend(
//                id = 2,
//                name = "Timothy",
//                username = "tim"
//            ),
//            Friend(
//                id = 3,
//                name = "Jonathan",
//                username = "jon"
//            )
//        )
//        user.friends.addAll(mockFriends)
//    }
//
//    fun insertEntry(rank: Int, entry: Entry) {
//        val currentUser = userLiveData.value!!
//        if (rank >= 0 && rank <= currentUser.rankings.length()) {
//            currentUser.rankings.insert(rank, entry)
//            userLiveData.postValue(currentUser)
//        } else {
//            System.err.println("Error: The provided rank $rank is out of bounds.")
//        }
//    }
//
//
//    fun updateEntry(entry: Entry) {
//        val currentUser = userLiveData.value!!
//        try {
//            currentUser.rankings.updateById(entry.id, entry)
//            userLiveData.postValue(currentUser)
//        } catch (e: NoSuchElementException) {
//            System.err.println("Error: The provided entry's entry id ${entry.id} does not exist.")
//        }
//    }
//
//    fun getRankingsClone(): RankingList {
//        val currentUser = userLiveData.value!!
//        return RankingList(currentUser.rankings.toList().map { it.copy() })
//    }
//
//    fun getFriends(): MutableList<Friend>? {
//        return userLiveData.value!!.friends
//    }
//
//    fun updateUserRankings(newRankings: RankingList) {
//        val currentUser = userLiveData.value!!
//        currentUser.let { user ->
//            user.rankings = newRankings
//            userLiveData.postValue(user) // Notify observers of the update
//        }
//    }
//
//    fun getNewEntryId(): Int{
//        val currentUser = userLiveData.value !!
//        currentUser.let { user ->
//            user.total_entry_count++
//            userLiveData.postValue(user) // Notify observers of the update
//        }
//        return currentUser.total_entry_count
//    }
//}
