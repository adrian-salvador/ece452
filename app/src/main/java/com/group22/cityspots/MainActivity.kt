@file:OptIn(ExperimentalComposeUiApi::class)

package com.group22.cityspots

import LoginScreen
import android.content.Intent
import android.net.Uri
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.group22.cityspots.ui.theme.CityHangoutsTheme
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.libraries.places.api.Places
import com.group22.cityspots.model.GoogleAuthUIClient
import com.group22.cityspots.ui.theme.CityHangoutsTheme
import com.group22.cityspots.view.AddEntryScreen
import com.group22.cityspots.view.EntryScreen
import com.group22.cityspots.view.FriendsScreen
import com.group22.cityspots.view.HomeScreen
import com.group22.cityspots.view.ProfileScreen
import com.group22.cityspots.view.RankingScreen
import com.group22.cityspots.viewmodel.MapViewModel
import com.group22.cityspots.viewmodel.SignInViewModel
import com.group22.cityspots.viewmodel.UserViewModel
import com.group22.cityspots.viewmodel.UserViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val googleAuthUIClient by lazy {
        GoogleAuthUIClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private lateinit var userViewModel: UserViewModel
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appInfo: ApplicationInfo = applicationContext.packageManager.getApplicationInfo(
            applicationContext.packageName,
            PackageManager.GET_META_DATA
        )
        val apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY")
        Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)
        mapViewModel = MapViewModel(applicationContext)
        setContent {
            CityHangoutsTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            val viewModel = viewModel<SignInViewModel>()
                            val state by viewModel.state.collectAsState()
                            val user = googleAuthUIClient.getSignedInUser()
                            var showDialog by remember { mutableStateOf(false) }
                            val context = LocalContext.current

                            LaunchedEffect(key1 = Unit) {
                                if(user  != null) {
                                    userViewModel = ViewModelProvider(this@MainActivity, UserViewModelFactory(user)).get(UserViewModel::class.java)
                                    userViewModel.saveUser(user, applicationContext)
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
                            LaunchedEffect(key1 = state.isSignInSuccessful) {
                                if (state.isSignInSuccessful && user!=null) {
                                    userViewModel = ViewModelProvider(this@MainActivity, UserViewModelFactory(user)).get(UserViewModel::class.java)
                                    userViewModel.saveUser(user, applicationContext)
                                    Toast.makeText(
                                        applicationContext,
                                        "Sign In Successful",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.navigate("home")
                                } else if (state.signInError != null) {
                                    showDialog = true;
                                }
                            }
                            LoginScreen(
                                state = state,
                                onSignInClick = {
                                    lifecycleScope.launch {
                                        val signInIntent = googleAuthUIClient.signIn()
                                        if (signInIntent == null){
                                            showDialog = true;
                                        }
                                        launcher.launch(
                                            IntentSenderRequest.Builder(
                                                signInIntent ?: return@launch
                                            ).build()
                                        )
                                    }
                                }
                            )
                            if (showDialog){
                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    title = { Text(text = "Google Account Required") },
                                    text = { Text(text = "This app requires a Google account for authentication. Would you like to add one now?") },
                                    confirmButton = {
                                        Button(
                                            onClick = {
                                                showDialog = false
                                                val addAccountIntent = Intent(Settings.ACTION_ADD_ACCOUNT)
                                                addAccountIntent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                                                context.startActivity(addAccountIntent)
                                            }
                                        ) {
                                            Text("Add Account")
                                        }
                                    },
                                    dismissButton = {
                                        Button(
                                            onClick = { showDialog = false }
                                        ) {
                                            Text("Cancel")
                                        }
                                    }
                                )
                            }
                        }
                        composable("home") { HomeScreen(navController, userViewModel) }
                        composable("ranking") { RankingScreen(navController, userViewModel) }
                        composable(
                            route = "entry/{entryId}",
                            arguments = listOf(navArgument("entryId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            EntryScreen(navController, backStackEntry, userViewModel)
                        }
                        composable("addEntry") { AddEntryScreen(navController, userViewModel, mapViewModel) }
                        composable("friends") { FriendsScreen(navController, userViewModel) }
                        composable("userProfile") {
                            ProfileScreen(
                                userLoginData = googleAuthUIClient.getSignedInUser(),
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




