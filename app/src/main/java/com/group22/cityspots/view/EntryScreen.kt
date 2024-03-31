package com.group22.cityspots.view

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.group22.cityspots.respository.Firestore
import com.group22.cityspots.viewmodel.EntryViewModel
import com.group22.cityspots.viewmodel.EntryViewModelFactory
import com.group22.cityspots.viewmodel.TripViewModel
import com.group22.cityspots.viewmodel.TripViewModelFactory
import com.group22.cityspots.viewmodel.UserViewModel

@Composable
fun EntryScreen(navController: NavController, navBackStackEntry: NavBackStackEntry, userViewModel: UserViewModel) {
    val user by userViewModel.userLiveData.observeAsState()
    val rankingScreenViewModel: EntryViewModel = viewModel(
        factory = EntryViewModelFactory(user!!.userId)
    )
    val entryId = navBackStackEntry.arguments!!.getString("entryId")
    println(entryId)
    val entries by rankingScreenViewModel.entriesLiveData.observeAsState()
    val currentEntry = entries?.find { entry -> entry.entryId == entryId }
    val tripViewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(user!!.userId)
    )
    val trips by tripViewModel.tripsLiveData.observeAsState()
    var selectedTrip by remember { mutableStateOf("") }
    trips?.forEach { trip ->
        if (trip.tripId == currentEntry?.tripId) {
            selectedTrip = trip.title
            return@forEach
        }
    }
    val context = LocalContext.current

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
                        for (image in currentEntry.pictures!!) {
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
                                text = String.format("%.2f", currentEntry.rating),
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
                                text = if (currentEntry.address.isNotEmpty()) currentEntry.address else "No Location Data"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    if (selectedTrip.isNotEmpty()) {
                        Text(
                            modifier = Modifier.padding(start = 20.dp),
                            style = MaterialTheme.typography.titleMedium,
                            text = "Trip"
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
                                Text(text = selectedTrip)
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))
                    }

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
                Button(
                    onClick = {
                        Firestore().deleteEntry(currentEntry, context)
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    colors = ButtonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White
                    )
                ){
                    Text(
                        text = "Delete"
                    )
                }
            }
        }
    }
}