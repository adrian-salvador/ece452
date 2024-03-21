@file:OptIn(ExperimentalComposeUiApi::class)

package com.group22.cityspots

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.group22.cityspots.ui.theme.CityHangoutsTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.auth.api.identity.Identity
import com.group22.cityspots.model.RankingList
import com.group22.cityspots.model.User
import com.group22.cityspots.model.GoogleAuthUIClient
import com.group22.cityspots.view.LoginScreen
import com.group22.cityspots.model.SignInViewModel
import com.group22.cityspots.view.ProfileScreen
import com.group22.cityspots.view.AddEntryScreen
import com.group22.cityspots.view.EntryScreen
import com.group22.cityspots.view.FriendsScreen
import com.group22.cityspots.view.HomeScreen
import com.group22.cityspots.view.RankingScreen
import com.group22.cityspots.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUIClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CityHangoutsTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

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
                        composable("login") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsState()
                            
                            LaunchedEffect(key1 = Unit) {
                                if(googleAuthUIClient.getSignedInUser() != null) {
                                    navController.navigate("home")
                                }
                            }
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.StartIntentSenderForResult(),
                                onResult = { result ->
                                    if (result.resultCode == RESULT_OK) {
                                        lifecycleScope.launch {
                                            val signInResult = googleAuthUIClient.signInWithIntent(
                                                intent = result.data ?: return@launch
                                            )
                                            viewModel.onSignInResult(signInResult)
                                        }
                                    }
                                }
                            )
                            LaunchedEffect(key1 = state.isSignInSuccessful ){
                                if (state.isSignInSuccessful){
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign In Successul",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigate("home")
                                }
                            }
                            LoginScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntent = googleAuthUIClient.signIn()
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntent ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                        }
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
                        composable("userProfile") {
                            ProfileScreen(
                                userData = googleAuthUIClient.getSignedInUser(),
                                onSignOut = {
                                    lifecycleScope.launch {
                                        googleAuthUIClient.signOut()
                                        Toast.makeText(
                                            applicationContext,
                                            "Signed Out",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        navController.navigate("login")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}




