package com.group22.cityspots.view

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.group22.cityspots.model.Entry
import com.group22.cityspots.model.GeoLocation
import com.group22.cityspots.model.Trip
import com.group22.cityspots.viewmodel.AddEntryViewModel
import com.group22.cityspots.viewmodel.AddEntryViewModelFactory
import com.group22.cityspots.viewmodel.MapViewModel
import com.group22.cityspots.viewmodel.UserViewModel
import com.group22.cityspots.viewmodel.TripViewModel
import com.group22.cityspots.viewmodel.TripViewModelFactory
import okhttp3.internal.wait


@Composable
fun AddEntryScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    userViewModel: UserViewModel,
    mapViewModel: MapViewModel
) {
    val user by userViewModel.userLiveData.observeAsState()

    var entryName by remember { mutableStateOf("") }
    var hasTitle by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var tripId by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("")}
    val tripViewModel: TripViewModel = viewModel(
        factory = TripViewModelFactory(user!!.userId)
    )
    val trips by tripViewModel.tripsLiveData.observeAsState()
    val entryId = navBackStackEntry.arguments?.getString("entryId")
    val addEntryViewModel: AddEntryViewModel = viewModel(
        factory = AddEntryViewModelFactory(user!!.userId)
    )

    val editEntry by addEntryViewModel.editEntry.observeAsState()
    val entries by addEntryViewModel.entriesLiveData.observeAsState()
    val currentEntry = entries?.find { entry -> entry.entryId == entryId }
    val tags = addEntryViewModel.tags.observeAsState(listOf())
//    if (entryId!=null){
//        val rating by addEntryViewModel.ratingLiveData.observeAsState(currentEntry!!.rating)
//    } else {
//    }
    val rating by addEntryViewModel.ratingLiveData.observeAsState(0.00)
    //var ranking = 0.00 // baseline
    var showRatingPopup by remember { mutableStateOf(false) }
    var showAddTrip by remember { mutableStateOf(false) }
    val showAddTripCallback: () -> Unit = { showAddTrip = true }
    val imageUris = remember { mutableStateListOf<Uri>() }
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { imageUris.add(it) }
        }
    )

    fun refreshTrips() {
        tripViewModel.refreshTrips()
    }

    var displayMap by remember { mutableStateOf(false) }

    val context = LocalContext.current
    println("entires size: "+ entries?.size)
    if (entries != null && editEntry == null){
        println("Entry id: " + entryId)
        if (entryId != null) {
            // this means we are in an edit
            //addEntryViewModel.updateEditEntry(entryId) CHECK IF NEEDED
            entryName = currentEntry!!.title
            hasTitle = true
            description = currentEntry.review
            //rating = currentEntry.rating
            //println("Current Entry Rating: " + currentEntry.rating)
            tripId = currentEntry.tripId
            //tags = currentEntry.tags
            //placeId = currentEntry.currentPlaceId
            address = currentEntry.address


            //pictures = null,
            //tags = tags.value,
        }
    }


    Box(
        modifier = Modifier
            .background(Color(0x2F84ABE4))
            .padding(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .padding(18.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LocationNameEntry(entryName, hasTitle, Modifier.weight(1F)) { name, hasName ->
                    entryName = name
                    hasTitle = hasName
                }

                Button(
                    onClick = { showRatingPopup = true },
                    modifier = Modifier
                        .wrapContentWidth()
                        .widthIn(max = 150.dp)
                ) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "No image available",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xfff8d675)
                    )
                    Text(String.format("%.2f", rating), style = MaterialTheme.typography.bodyLarge)

                }
            }

            DisplayLocation(
                onClick = {displayMap = !displayMap},
                mapViewModel = mapViewModel
            )

            TripEntry(trips ?: emptyList<Trip>(), tripId, user!!.userId, showAddTripCallback) { newTripId ->
                tripId = newTripId
            }

            DescriptionEntry(description) { newDescription ->
                description = newDescription
            }

            TagEditorFragment(
                tags = tags,
                addTag = { tag -> addEntryViewModel.addTag(tag) },
                removeTag = { tag -> addEntryViewModel.removeTag(tag) }
            )

            // Display Selected Images
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 10.dp)
            ) {
                imageUris.forEach { uri ->
                    CroppedSquareImage(uri)
                }
            }

            //Upload Image Button
            Button(
                onClick = { pickImagesLauncher.launch("image/*") },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text("Upload Images")
            }

            //Submit Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) {
                Button(onClick = {
                    if (hasTitle) {
                        if (entryId != null) {
                            val entryDetails = Entry(
                                entryId = entryId,
                                title = entryName,
                                pictures = null,
                                review = description,
                                tags = tags.value,
                                tripId = tripId,
                                placeId = mapViewModel.currentPlaceId,
                                address = mapViewModel.currentAddress,
                                rating = rating.toDouble(),
                                userId = user!!.userId
                            )
                            addEntryViewModel.updateEditEntry(entryId, entryDetails)
                        }
                        else {
                            val entryDetails = Entry(
                                entryId = null,
                                title = entryName,
                                pictures = null,
                                review = description,
                                tags = tags.value,
                                tripId = tripId,
                                placeId = mapViewModel.currentPlaceId,
                                address = mapViewModel.currentAddress,
                                rating = rating.toDouble(),
                                userId = user!!.userId
                            )
                            addEntryViewModel.uploadImagesAndCreateEntry(imageUris, entryDetails, context )
                        }
                        navController.popBackStack()
                    }else {
                        Toast.makeText(context, "Please add an Activity Name", Toast.LENGTH_LONG).show()
                    }
                }) {
                    Text("Add Entry")
                }
            }
        }
    }

    if (showRatingPopup) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = { showRatingPopup = false }),
            contentAlignment = Alignment.Center
        ) {
            RatingSelectionPopup(addEntryViewModel) {
                showRatingPopup = false
            }
        }
    }

    if (showAddTrip) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = { showAddTrip = false }),
            contentAlignment = Alignment.Center
        ) {
            AddTripPopup(user!!.userId) {
                showAddTrip = false
                refreshTrips()
            }
        }
    }

    if (displayMap) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            MapScreen(
                close = {displayMap = false},
                viewModel = mapViewModel
            )
        }
    }
}

 @Composable
fun CroppedSquareImage(imageUri: Uri) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = imageUri)
            .apply<ImageRequest.Builder>(block = fun ImageRequest.Builder.() {
                transformations(CircleCropTransformation())
            }).build()
    )
    Image(
        painter = painter,
        contentDescription = "Cropped Image",
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(4.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun LocationNameEntry(entryName: String, hasTitle: Boolean, modifier: Modifier, onValueChange: (String, Boolean) -> Unit) {
    Box (modifier = modifier){
        if (!hasTitle) {
            Text(
                text = "Activity Name",
                color = Color.Gray,
                style = MaterialTheme.typography.titleLarge
            )
        }
        BasicTextField(
            value = entryName,
            onValueChange = { newValue ->
                onValueChange(newValue, newValue.isNotEmpty())
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            textStyle = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun DescriptionEntry(description: String, onValueChange: (String) -> Unit) {
    var descriptionvalue by remember { mutableStateOf(description) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 12.dp)
    ) {
        TextField(
            value = descriptionvalue,
            onValueChange = { descriptionvalue = it },
            placeholder = { Text("Description") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color(0xFF84ABE4),
                unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )
    }
}

 @Composable
 fun TripEntry(trips: List<Trip>, tripId: String, userId: String, showAddTripCallback: () -> Unit, onValueChange: (String) -> Unit) {
     var expanded by remember { mutableStateOf(false) }
     var selectedTrip by remember { mutableStateOf(tripId) }
     var triggerRowWidth by remember { mutableIntStateOf(0) }
     trips.forEach { trip ->
         if (trip.tripId == tripId) {
             selectedTrip = trip.title
             println("Selected Trip Is : " + selectedTrip)
             return@forEach
         }
     }

     Row(
         modifier = Modifier
             .fillMaxWidth()
             .padding(top = 15.dp)
             .clip(RoundedCornerShape(15.dp))
             .background(Color.White)
     ) {
         Column(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(vertical = 12.dp),
         ) {
             Text(
                 text = "Trip:",
                 modifier = Modifier.padding(start = 18.dp)
             )
             Spacer(modifier = Modifier.height(8.dp))

                 Row(verticalAlignment = Alignment.CenterVertically) {
                     Box(
                         contentAlignment = Alignment.CenterStart,
                         modifier = Modifier
                             .onSizeChanged { newSize -> triggerRowWidth = newSize.width }
                             .weight(1f)
                             .clip(RoundedCornerShape(4.dp))
                             .background(Color.White)
                             .clickable(onClick = { expanded = true })
                             .padding(horizontal = 18.dp, vertical = 8.dp)
                             .border(
                                 width = 1.dp,
                                 color = Color.Gray,
                                 shape = RoundedCornerShape(8.dp)
                             )
                             .padding(
                                 horizontal = 12.dp,
                                 vertical = 8.dp
                             )
                     ) {
                         Row(
                             verticalAlignment = Alignment.CenterVertically,
                         ) {
                             Text(
                                 selectedTrip,
                                 modifier = Modifier.weight(1f)
                             )
                             Icon(
                                 imageVector = Icons.Default.ArrowDropDown,
                                 contentDescription = "Dropdown",
                                 modifier = Modifier
                                     .size(24.dp)
                                     .align(Alignment.CenterVertically)
                             )
                         }
                     }
                     Button(
                         onClick = { showAddTripCallback() },
                         shape = RoundedCornerShape(8.dp),
                         modifier = Modifier
                             .padding(end = 18.dp)
                     ) {
                         Icon(
                             imageVector = Icons.Filled.Add,
                             contentDescription = null,
                         )
                     }
                 }
             Row {
                 Spacer(modifier = Modifier.width(18.dp))
                 Box(modifier = Modifier.width(300.dp)){
                     DropdownMenu(
                         expanded = expanded,
                         onDismissRequest = { expanded = false },
                         modifier = Modifier
                             .background(Color.White)
                             .width(with(LocalDensity.current) { triggerRowWidth.toDp() - 36.dp })
                     ) {
                         trips.forEach { trip ->
                             DropdownMenuItem(
                                 modifier = Modifier
                                     .padding(vertical = 8.dp)
                                     .clip(RoundedCornerShape(8.dp)),
                                 text = { Text(text = trip.title) },
                                 onClick = {
                                     onValueChange(trip.tripId!!)
                                     expanded = false
                                 }
                             )
                             if (trip != trips.last()) {
                                 HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 15.dp))
                             }
                         }
                     }
                 }
             }
         }
     }
 }

 @Composable
fun TagEntry(tags: MutableList<String>) {
    var newTag by remember { mutableStateOf("") }

    TextField(
        value = newTag,
        onValueChange = {
            newTag = it
        },
        placeholder = {
            Text(
                text = "Filter Tags...",
                color = Color(0xFFb2c5ff),
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(start = 16.dp)
            )
        },
        trailingIcon = {
            IconButton(onClick = {
                if (newTag.isNotBlank()) {
                    tags.add(newTag)
                    newTag = ""
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color(0xFFF3F8FE),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = Color(0xFF176FF2),
        ),
        shape = RoundedCornerShape(40.dp),
        textStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}

@Composable
fun DisplayLocation(onClick: () -> Unit, mapViewModel: MapViewModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFDBE8F9),
            contentColor = Color(0xFF176FF2)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Log.d("hehexd", "AddEntryScreen: mapViewModel.currentPlace = ${mapViewModel.currentPlace}")
                Text(
                    text = mapViewModel.currentPlace,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}


@Composable
fun DisplayTags(tags: List<String>) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        tags.forEach { tag ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFDBE8F9),
                contentColor = Color(0xFF176FF2)
            ) {
                Text(
                    text = tag,
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}