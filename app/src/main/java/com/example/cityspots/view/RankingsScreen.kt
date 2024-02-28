package com.example.cityspots.view

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cityspots.viewmodel.UserViewModel

@Composable
fun RankingScreen(navController: NavController, userViewModel: UserViewModel = viewModel()) {
//    val user = userViewModel.userLiveData.observeAsState().value
//    user?.entries?.let { entries ->
//        LazyColumn {
//            items(entries) { entry ->
//                Text(entry.content, modifier = Modifier.padding(16.dp))
//            }
//        }
//    }
}
