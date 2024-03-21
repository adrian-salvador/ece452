@file:OptIn(ExperimentalComposeUiApi::class)

package com.group22.cityspots

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.group22.cityspots.ui.theme.CityHangoutsTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.group22.cityspots.view.AppNavigator
import com.group22.cityspots.view.AuthActivity
import com.group22.cityspots.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser == null) {
            // User is not signed in, redirect to AuthActivity for authentication
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }
        setContent {
            CityHangoutsTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavigator()
                }
            }
        }
    }
}




