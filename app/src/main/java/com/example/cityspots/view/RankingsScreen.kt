package com.example.cityspots.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cityspots.viewmodel.UserViewModel

@Composable
fun RankingScreen(navController: NavController, userViewModel: UserViewModel = viewModel()) {
    val user by userViewModel.userLiveData.observeAsState()
    val entries = user?.entries?.sortedBy { it.ranking }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy((-60).dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            entries?.forEach { entry ->
                item {
                    Column(
                        modifier = Modifier
                        .padding(innerPadding)
                        .height(250.dp)
                        .width(250.dp)
                        .clip(RoundedCornerShape(5))
                        .shadow(elevation = 2.dp)
                        .clickable
                        {
                            navController.navigate("entry/${entry.id}")
                        }) {
                        Box(
                            modifier = Modifier
                                .height(190.dp)
                                .width(190.dp)
                        ) {
                            AsyncImage(
                                model = entry.pictures[0],
                                contentDescription = "Image of entry",
                                modifier = Modifier
                                    .padding(5.dp)
                                    .align(Alignment.Center)
                                    .clip(RoundedCornerShape(5)),
                                contentScale = ContentScale.FillBounds
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(entry.ranking.toString())
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(entry.content)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
