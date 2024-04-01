package com.group22.cityspots.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.group22.cityspots.viewmodel.MapViewModel
import com.group22.cityspots.viewmodel.RankingScreenViewModel
import com.group22.cityspots.viewmodel.RankingScreenViewModelFactory
import com.group22.cityspots.viewmodel.TripViewModel
import com.group22.cityspots.viewmodel.TripViewModelFactory

@Composable
fun FriendEntryScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    mapViewModel: MapViewModel
) {
    val userId = navBackStackEntry.arguments!!.getString("userId")
    val username = navBackStackEntry.arguments!!.getString("userName")
    val rankingScreenViewModel: RankingScreenViewModel = viewModel(
        factory = RankingScreenViewModelFactory(userId!!)
    )
    val entryId = navBackStackEntry.arguments!!.getString("entryId")
    println(entryId)
    val entries by rankingScreenViewModel.entriesLiveData.observeAsState()
    val currentEntry = entries?.find { entry -> entry.entryId == entryId }
    val tripViewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(userId)
    )
    val trips by tripViewModel.tripsLiveData.observeAsState()
    var selectedTrip by remember { mutableStateOf("") }
    trips?.forEach { trip ->
        if (trip.tripId == currentEntry?.tripId) {
            selectedTrip = trip.title
            return@forEach
        }
    }

    var showMap by remember { mutableStateOf(false) }

    Scaffold (
        bottomBar = {
            if (!showMap) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        currentEntry?.let { entry ->
            Box(modifier = Modifier.padding(innerPadding)) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        modifier = Modifier.padding(start = 20.dp),
                        style = MaterialTheme.typography.titleLarge,
                        text = "${username}'s Entry"
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    // Images
                    Box() {
                        Row(
                            Modifier
                                .horizontalScroll(rememberScrollState())
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (image in entry.pictures!!) {
                                Box(
                                    modifier = Modifier
                                        // change height and width to be dynamic in the future?
                                        .height(250.dp)
                                        .width(368.dp)
                                        .padding(end = 8.dp)
                                ) {
                                    AsyncImage(
                                        model = image,
                                        contentDescription = "Image of entry",
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .fillMaxWidth()
                                            .fillMaxHeight(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                            }
                        }


                        // Trip
                        if (selectedTrip.isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .padding(start = 25.dp, top = 10.dp)
                                    .align(Alignment.TopStart)
                            ) {
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.Black.copy(alpha = 0.5f))
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(text = "Trip: $selectedTrip", color = Color.White)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    //Entry Title
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Text(
                                style = MaterialTheme.typography.titleLarge,
                                text = entry.title
                            )
                        }
                        Spacer(Modifier.width(20.dp))
                        Spacer(Modifier.weight(1f))
                        RatingBubble(rating = entry.rating)
                    }
                    Surface(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .clickable {
                                if (entry.address.isNotEmpty()) showMap = true
                            },
                        color = Color(0xFFDBE8F9),
                        contentColor = Color(0xFF176FF2),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Location",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.padding(horizontal = 2.dp))
                            Text(
                                text = if (entry.address.isNotEmpty()) entry.address else "No Location Data",
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))


                    // Your Tags

                    Row(
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        entry.tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFDBE8F9),
                                contentColor = Color(0xFF176FF2),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp, vertical = 5.dp
                                    )
                                ) {
                                    Text(
                                        text = tag,
                                        style = TextStyle(fontWeight = FontWeight.Bold),
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    // Your Review

                    if (entry.review.isNotEmpty()) {
                        Row {
                            Spacer(modifier = Modifier.padding(start = 20.dp))

                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White)
                                    .padding(20.dp)
                                    .width(310.dp)
                            ) {
                                Text(text = entry.review)
                            }
                        }
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }

                if (showMap) {
                    MapScreen(mapViewModel, LatLng(currentEntry?.latitude!!, currentEntry?.longitude!!)) {
                        showMap = false
                    }
                }
            }
        }
    }
}