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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
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
    val duplicateEntries by entryScreenViewModel.duplicateEntries.observeAsState()
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
                    // Images
                    Box(){
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
                            Row (modifier = Modifier
                                .padding(start = 25.dp, top = 10.dp)
                                .align(Alignment.TopStart)){
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
                    Row (
                        modifier = Modifier.padding( horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
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

                    if (entry.review.isNotEmpty()){
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



                    // Other's Reviews
                    Row(
                        modifier = Modifier.padding(horizontal =  20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            style = MaterialTheme.typography.titleMedium,
                            text = "Other Location Reviews"
                        )
                        Spacer(Modifier.weight(1f))
                        RatingBubble(if (duplicateEntries?.isNotEmpty() == true) {
                            val allEntries = duplicateEntries!! + entry
                            allEntries.map { it.rating }.average()
                        } else {
                            entry.rating
                        })
                    }
                    

                    Spacer(modifier = Modifier.height(10.dp))
                    duplicateEntries?.forEach{ otherEntry ->
                        Row(
                            modifier = Modifier.padding(horizontal =  20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                style = MaterialTheme.typography.titleSmall,
                                text = otherEntry.title,
                                fontSize = 18.sp,
                            )
                            Spacer(Modifier.weight(1f))
                            RatingBubbleSmall(otherEntry.rating)
                        }

                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 10.dp,horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ){
                            otherEntry.pictures?.forEach { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Image of entry",
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .height(100.dp)
                                        .width(80.dp),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        if(otherEntry.review.isNotEmpty()){
                            Column (modifier = Modifier.padding(20.dp)){
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.LightGray)
                                        .padding(20.dp)
                                        .width(310.dp)
                                ) {
                                    Text(text = otherEntry.review)
                                }
                            }
                        }
                    }
                }
                Button(
                    onClick = {
                        navController.navigate("addEntry/${entry.entryId}")
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp),
                ){
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "No image available",
                        modifier = Modifier.size(25.dp),
                        tint = Color.White
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

@Composable
fun RatingBubble(rating: Double) {
    Row(
        modifier = Modifier
            .background(color = Color(0xFF445E91), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Star,
            contentDescription = "Rating star",
            modifier = Modifier.size(18.dp),
            tint = Color(0xFFF8D675)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format("%.2f", rating),
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
fun RatingBubbleSmall(rating: Double) {
    Row(
        modifier = Modifier
            //.background(color = Color(0xFF445E91), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Filled.Star,
            contentDescription = "Rating star",
            modifier = Modifier.size(18.dp),
            tint = Color(0xFFF8D675)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = String.format("%.2f", rating),
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black),
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}