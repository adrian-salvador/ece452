package com.example.cityspots.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cityspots.model.RankingList
import com.example.cityspots.model.User
import com.example.cityspots.viewmodel.AddEntryScreenViewModel
import com.example.cityspots.viewmodel.UserViewModel

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val user = User(
        id = 123,
        name = "Alice",
        rankings = RankingList(mutableListOf()),
        total_entry_count = 0,
        friends = mutableListOf()
    )
    val userViewModel = UserViewModel(user)
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {LoginScreen(navController, userViewModel) }
        composable("home") { HomeScreen(navController, userViewModel) }
        composable("ranking") { RankingScreen(navController, userViewModel) }
        composable(
            route = "entry/{entryId}",
            arguments = listOf(navArgument("entryId") { type = NavType.IntType })
        ) { backStackEntry ->
            EntryScreen(navController, backStackEntry, userViewModel)
        }
        composable("addEntry") { AddEntryScreen(navController, userViewModel) }
        composable("friends") { FriendsScreen(navController, userViewModel) }
    }
}