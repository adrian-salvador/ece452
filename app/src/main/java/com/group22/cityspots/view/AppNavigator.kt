package com.group22.cityspots.view

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.group22.cityspots.model.RankingList
import com.group22.cityspots.model.User
import com.group22.cityspots.viewmodel.AddEntryScreenViewModel
import com.group22.cityspots.viewmodel.UserViewModel

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