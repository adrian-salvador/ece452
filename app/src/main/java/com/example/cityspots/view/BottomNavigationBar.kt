package com.example.cityspots.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        "home" to Icons.Default.Home,
        "ranking" to Icons.Default.List,
        "addEntry" to Icons.Default.Add,
        "friends" to Icons.Default.Face,
        "userProfile" to Icons.Default.Person
    )

    NavigationBar {
        val currentRoute = navController.currentDestination?.route
        items.forEach { (route, icon) ->
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = null) },
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route)
                }
            )
        }
    }
}