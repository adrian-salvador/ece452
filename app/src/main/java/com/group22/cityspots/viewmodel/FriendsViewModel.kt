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

class FriendsViewModel(private val userId: String) : ViewModel() {
    private var originalFriends = listOf<User>()
    private val _friendsLiveData = MutableLiveData<List<User>>()
    private val _originalFriendUser =  MutableLiveData<Friends>()
    val friendUser = _originalFriendUser
    val friendsLiveData: LiveData<List<User>> = _friendsLiveData

    init {
        loadFriends()
    }

    private fun loadFriends() {
        viewModelScope.launch {
            val user = Firestore().getFriendByUserId(userId)
            println("User $user")
            _originalFriendUser.value = user!!
            val friends = Firestore().getFriendsByUserIds(user.friendIDs!!)
            originalFriends = friends
            _friendsLiveData.postValue(originalFriends)
        }
    }

    fun addFriend(email: String, user: User, context: Context) {

        viewModelScope.launch {
            Firestore().sendFriendReq(email, user, context)
        }

    }

    fun modFriendReq(email: String, user: User, action: String, context: Context) {
        viewModelScope.launch {
            if (action == "accept") {
                Firestore().modFriendReq(email, user, true, context)
            }
            else {
                Firestore().modFriendReq(email, user, false, context)
            }
        }
    }
}

class FriendsViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FriendsViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}