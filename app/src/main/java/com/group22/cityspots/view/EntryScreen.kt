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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.group22.cityspots.respository.Firestore
import com.group22.cityspots.viewmodel.AddEntryViewModel
import com.group22.cityspots.viewmodel.AddEntryViewModelFactory
import com.group22.cityspots.viewmodel.EntryScreenViewModel
import com.group22.cityspots.viewmodel.EntryScreenViewModelFactory
import com.group22.cityspots.viewmodel.EntryViewModel
import com.group22.cityspots.viewmodel.EntryViewModelFactory
import com.group22.cityspots.viewmodel.MapViewModel
import com.group22.cityspots.viewmodel.TripViewModel
import com.group22.cityspots.viewmodel.TripViewModelFactory
import com.group22.cityspots.viewmodel.UserViewModel


@Composable
fun EntryScreen(navController: NavController, navBackStackEntry: NavBackStackEntry, userViewModel: UserViewModel, mapViewModel: MapViewModel) {
    val user by userViewModel.userLiveData.observeAsState()
    val entryId = navBackStackEntry.arguments!!.getString("entryId")
    val entryScreenViewModel: EntryScreenViewModel = viewModel(
        factory = entryId?.let { EntryScreenViewModelFactory(it) }
    )
    val currentEntry by entryScreenViewModel.entry.observeAsState()
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
    var showMap by remember { mutableStateOf(false) }
    val context = LocalContext.current
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
                                modifier = Modifier
                                    .padding(start = 20.dp)
                                    .width(240.dp),
                                style = MaterialTheme.typography.titleLarge,
                                text = entry.title
                            )
                        }

                        Spacer(Modifier.width(20.dp))

                        Button(
                            onClick = {},
                            modifier = Modifier
                                .wrapContentWidth()
                                .widthIn(max = 100.dp)
                        ) {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "No image available",
                                modifier = Modifier.size(15.dp),
                                tint = Color(0xfff8d675)
                            )
                            Text(String.format("%.2f", entry.rating), style = MaterialTheme.typography.bodyMedium)

                        }

                        Spacer(Modifier.padding(end = 20.dp))
                    }
                    Surface(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .clickable {
                               showMap = true
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
                                .width(310.dp)
                        ) {
                            Text(text = entry.review)
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
                        for (tag in entry.tags) {
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(40.dp))
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

                    Spacer(modifier = Modifier.height(10.dp))

                    //Entry Button
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp)
                    ) {

                        Button(onClick = {
                            navController.navigate("addEntry/${entry.entryId}")
                        }) {
                            Text("Edit Entry")
                        }
                    }
                }
                Button(
                    onClick = {
                        Firestore().deleteEntry(entry, context)
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

                if (showMap) {
                    MapScreen(mapViewModel, LatLng(currentEntry?.latitude!!, currentEntry?.longitude!!)) {
                        showMap = false
                    }
                }
            }
        }
    }
}