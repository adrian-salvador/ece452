package com.example.cityspots.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.group22.cityspots.viewmodel.UserViewModel
import kotlin.random.Random

@Composable
fun LoginScreenOld(navController: NavController, userViewModel: UserViewModel) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Simulate login logic
            // In a real app, you would authenticate against your backend and fetch user data
            if (true || username.isNotEmpty() && password.isNotEmpty()) {
                val userId = (Random.nextInt(1, 11))
                userViewModel.loginUser(userId) // Pass the logged-in user's ID or username
                navController.navigate("home") // Navigate to the home screen upon successful login
            }
        }) {
            Text("Log In")
        }
    }
}