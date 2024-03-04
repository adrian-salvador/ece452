package com.example.cityspots.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cityspots.viewmodel.UserViewModel

@Composable
fun AppNavigator(userViewModel: UserViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
//        composable("login") {
//            LoginScreen(navController) { userId ->
//                userViewModel.loginUser(userId) // Update the logged-in user in ViewModel
//            }
//        }
        composable("home") { HomeScreen(navController, userViewModel) }
        composable("ranking") { RankingScreen(navController, userViewModel) }
        composable("entry/{entryId}") { EntryScreen(navController, userViewModel) }
        composable("addEntry") { AddEntryScreen(navController, userViewModel) }
    }
}