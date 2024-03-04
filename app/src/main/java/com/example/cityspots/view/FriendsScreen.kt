package com.example.cityspots.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cityspots.model.Friend
import com.example.cityspots.model.User
import com.example.cityspots.viewmodel.UserViewModel

@Composable
fun FriendsScreen(navController: NavController, userViewModel: UserViewModel) {
    val friends = mutableListOf<Friend>().apply {
        addAll(userViewModel.getFriends() ?: emptyList())
    }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Friends",
                    style = MaterialTheme.typography.titleMedium
                )
                friends.forEach() { friend ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Name: ${friend.name}")
                    Text("Username: ${friend.username}")        }
            }
        }
    }
}