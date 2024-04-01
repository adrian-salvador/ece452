package com.group22.cityspots.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.group22.cityspots.model.User
import com.group22.cityspots.model.Friends
import com.group22.cityspots.respository.Firestore
import kotlinx.coroutines.launch

class FriendsViewModel(private val userEmail: String) : ViewModel() {
    private var originalFriends = listOf<User>()
    private val _friendsLiveData = MutableLiveData<List<User>>()
    private val _friendClassLiveData =  MutableLiveData<Friends>()
    val friendUser = _friendClassLiveData
    val friendsLiveData: LiveData<List<User>> = _friendsLiveData

    init {
        loadFriends()
    }

    private fun loadFriends() {
        viewModelScope.launch {
            val user = Firestore().getFriendByUserId(userEmail)
            println("User $user")
            _friendClassLiveData.value = user!!
            val friends = Firestore().getFriendsByUserIds(user.friendIDs!!)
            originalFriends = friends
            _friendsLiveData.postValue(originalFriends)
        }
    }

    fun addFriend(email: String, user: User, context: Context) {
        // Safely add to sentRequests and update LiveData
        _friendClassLiveData.value?.let { friend ->
            if (friend.sentRequests == null) {
                friend.sentRequests = mutableListOf()
            }
            friend.sentRequests?.add(email)
            _friendClassLiveData.postValue(friend) // Post the entire updated object
        }

        viewModelScope.launch {
            Firestore().sendFriendReq(email, user, context)
        }
    }

    fun modFriendReq(email: String, user: User, action: String, context: Context) {
        // Safely remove from recvRequests and update LiveData
        _friendClassLiveData.value?.let { friend ->
            friend.recvRequests?.remove(email)
            if (action == "accept") {
                Firestore().modFriendReq(email, user, true, context)
                if (friend.friendIDs == null) {
                    friend.friendIDs = mutableListOf()
                }
                friend.friendIDs?.add(email)
                friend.sentRequests?.remove(email)
                friend.recvRequests?.remove(email)

                // Since getFriendsByUserIds is a suspend function, call it inside a coroutine
                viewModelScope.launch {
                    friend.friendIDs?.let { ids ->
                        val friends = Firestore().getFriendsByUserIds(ids)
                        _friendsLiveData.postValue(friends) // Post the entire updated list
                    }
                }
            } else {
                Firestore().modFriendReq(email, user, false, context)
            }
            _friendClassLiveData.postValue(friend) // Post the entire updated object
        }
    }

}

class FriendsViewModelFactory(private val userEmail: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendsViewModel(userEmail) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}