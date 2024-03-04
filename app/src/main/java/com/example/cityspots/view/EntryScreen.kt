package com.example.cityspots.view

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.cityspots.model.User
import com.example.cityspots.viewmodel.UserViewModel

@Composable
fun EntryScreen(navController: NavController, navBackStackEntry: NavBackStackEntry, userViewModel: UserViewModel) {
    val user by userViewModel.userLiveData.observeAsState()
    val entryId = navBackStackEntry.arguments!!.getInt("entryId")
    println(entryId)
    val currentEntry = user!!.rankings.get(entryId)
    val currentEntryRank = user!!.rankings.getRankById(entryId)

    Scaffold (
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        if (currentEntry != null) {
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (image in currentEntry.pictures) {
                            Box(
                                modifier = Modifier
                                    // change height and width to be dynamic in the future?
                                    .height(250.dp)
                                    .width(368.dp)
                                    .padding(end = 8.dp)
                            ) {
                                AsyncImage(
                                    modifier = Modifier.clip(RoundedCornerShape(5)),
                                    model = image,
                                    contentDescription = "image"
                                )
                            }

                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Row {
                        Box {
                            Text(
                                modifier = Modifier.padding(start = 20.dp),
                                style = MaterialTheme.typography.titleLarge,
                                text = currentEntry.title
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Box(
                            Modifier
                                .size(55.dp)
                                .clip(CircleShape)
                                .background(Color.Blue)
                                .padding(12.dp)
                                .aspectRatio(1f)
                        ) {
                            Text(
                                text = "#${currentEntryRank}",
                                color = Color.White,
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                        Spacer(Modifier.padding(end = 20.dp))
                    }

                    Row {
                        Box(
                            Modifier.padding(start = 20.dp)
                        ) {
                            Text(
                                text = "${currentEntry.geoLocation.longitude}°, " +
                                        "${currentEntry.geoLocation.latitude}°"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        modifier = Modifier.padding(start = 20.dp),
                        style = MaterialTheme.typography.titleMedium,
                        text = "Reviews"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row {
                        Spacer(modifier = Modifier.padding(start = 20.dp))

                        Box(
                            Modifier
                                .clip(RoundedCornerShape(10))
                                .background(Color.LightGray)
                                .padding(20.dp)
                        ) {
                            Text(text = currentEntry.review)
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        modifier = Modifier.padding(start = 20.dp),
                        style = MaterialTheme.typography.titleMedium,
                        text = "Tags"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        for (tag in currentEntry.tags) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(10))
                                    .background(Color.LightGray)
                                    .padding(14.dp)
                            ) {
                                Text(
                                    text = tag
                                )
                            }
                            Spacer(Modifier.padding(end = 15.dp))
                        }
                    }
                }
            }
        }
    }
}