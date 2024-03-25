package com.group22.cityspots.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.group22.cityspots.respository.Firestore
import com.group22.cityspots.viewmodel.AddEntryViewModel
import com.group22.cityspots.viewmodel.AddEntryViewModelFactory
import com.group22.cityspots.viewmodel.RankingScreenViewModel
import com.group22.cityspots.viewmodel.RankingScreenViewModelFactory
import com.group22.cityspots.viewmodel.UserViewModel

@Composable
fun RankingScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.userLiveData.observeAsState()
    val rankingScreenViewModel: RankingScreenViewModel = viewModel(
        factory = RankingScreenViewModelFactory(user!!.userId)
    )
    val entries by rankingScreenViewModel.entriesLiveData.observeAsState()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy((-60).dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            entries?.forEachIndexed() { index, entry ->
                item {
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .height(250.dp)
                            .width(250.dp)
                            .clip(RoundedCornerShape(5))
                            .shadow(elevation = 2.dp)
                            .clickable {
                                navController.navigate("entry/${entry.entryId}")
                            }
                    ) {
                        if (entry.pictures?.isNotEmpty() == true) {
                            Box(
                                modifier = Modifier
                                    .height(190.dp)
                                    .width(190.dp)
                            ) {
                                AsyncImage(
                                    model = entry.pictures.first(),
                                    contentDescription = "Image of entry",
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .align(Alignment.Center)
                                        .clip(RoundedCornerShape(5)),
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        } else {
                            // Displaying "no image" icon
                            Box(
                                modifier = Modifier
                                    .height(190.dp)
                                    .width(190.dp)
                                    .padding(5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "No image available",
                                    modifier = Modifier.size(100.dp),
                                    tint = Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("#${index + 1} ${entry.title}")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}