package com.group22.cityspots.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.group22.cityspots.model.Entry
import com.group22.cityspots.viewmodel.EntryViewModel
import com.group22.cityspots.viewmodel.EntryViewModelFactory
import com.group22.cityspots.viewmodel.MapViewModel
import com.group22.cityspots.viewmodel.UserViewModel

@Composable
fun HomeScreen(navController: NavController, userViewModel: UserViewModel, mapViewModel: MapViewModel) {
    // edit to grab cities from firebase
    val user by userViewModel.userLiveData.observeAsState()
    val entryScreenViewModel: EntryViewModel = viewModel(
        factory = EntryViewModelFactory(user!!)
    )

    val cities by userViewModel.citiesLiveData.observeAsState(mutableListOf())
    var selectedCity by remember { mutableStateOf(
        if (userViewModel.lastSelectedCity.value?.isNotEmpty() == true) {
            entryScreenViewModel.loadCityEntries(userViewModel.lastSelectedCity.value!!)
            userViewModel.lastSelectedCity.value!!
        }
        else "No City Selected"
    ) }

    var addCityModalVisible by remember { mutableStateOf(false)}

    // entries within the same city
    val entries by entryScreenViewModel.cityEntriesLiveData.observeAsState()
    var entriesToDisplay by remember{ mutableStateOf(emptyList<Entry>()) }

    if (entries != null) {
        entriesToDisplay = entries as List<Entry>

        // get and group all entries with the same placeId
        val entriesGrouped = entriesToDisplay.groupBy { it.placeId }

        // remove all entries within entriesGroup from entriesToDisplay
        entriesToDisplay = entriesToDisplay.filter { it.placeId !in entriesGrouped.keys }

        // create a new list to store entries to be added back
        val entriesToAddToDisplay = mutableListOf<Entry>()

        // iterate through each group and get the average rating + aggregate information
        entriesGrouped.forEach { (_, entriesInGrouped) ->

            val averageRating = entriesInGrouped.map { it.rating }.average()
            val title = entriesInGrouped.first().title
            val entryId = entriesInGrouped.first().entryId
            val pictures = entriesInGrouped.map { it ->
                it.pictures ?: emptyList()
            }.flatten()
            val reviews = entriesInGrouped.map { it.review }
            val tags = entriesInGrouped.map { it.tags }.flatten()
            val placeId = entriesInGrouped.first().placeId
            val address = entriesInGrouped.first().address

            // create a new entry with information above and store into entriesToAddToDisplay
            entriesToAddToDisplay.add(Entry(
                entryId = entryId,
                title = title,
                pictures = pictures,
                review = reviews.joinToString(),
                tags = tags,
                placeId = placeId,
                address = address,
                rating = averageRating
            ))
        }

        // add all entries in entriesToAddToDisplay back to entriesToDisplay
        entriesToDisplay = entriesToDisplay + entriesToAddToDisplay

        // sort entries to display by rating
        entriesToDisplay = entriesToDisplay.sortedByDescending { it.rating }
    }

    var expanded by remember { mutableStateOf(false) }

    val currentContext = LocalContext.current

    val onCloseClicked: (city: String) -> Unit = {
        addCityModalVisible = false
        userViewModel.addUserCity(it, currentContext)
        selectedCity = it
        userViewModel.updateLastSelectedCity(it)
    }

    if (selectedCity == "No City Selected" && cities.isNotEmpty()){
        selectedCity = cities[0]
        userViewModel.updateLastSelectedCity(selectedCity)
        entryScreenViewModel.loadCityEntries(selectedCity)
    }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)) {
                // Check if user data is available and display the welcome message
//                user?.let {
//                    Text(
//                        text = "Welcome ${it.name}!",
//                        style = MaterialTheme.typography.titleLarge
//                    )
//                }

                Spacer(modifier = Modifier.height(16.dp))

                Row (verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { expanded = !expanded }
                        ) {
                            Text(
                                text = (
                                    if (selectedCity == "No City Selected") "No City Selected"
                                    else selectedCity.split(",").first().trim()
                                ),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "dropdown"
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            cities.forEach { city ->
                                DropdownMenuItem(
                                    text = { Text(city) },
                                    onClick = {
                                        selectedCity = city
                                        userViewModel.updateLastSelectedCity(selectedCity)
                                        entryScreenViewModel.loadCityEntries(selectedCity)
                                        expanded = false
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "+ Add City",
                                        style = TextStyle(fontWeight = FontWeight.Bold)
                                    )
                                },
                                onClick = { addCityModalVisible = true }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row {
                    if (cities.isEmpty()) {
                        Button(onClick = { addCityModalVisible = true } ) {
                            Text("Add City")
                        }

                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    Button(onClick = { navController.navigate("ranking") }) {
                        Text("Go to Ranking")
                    }

                    if (selectedCity != "No City Selected") {
                        Spacer(modifier = Modifier.width(16.dp))

                        // Button to delete city
                        Button(
                            onClick = {
                            userViewModel.removeUserCity(selectedCity, currentContext)
                            selectedCity = if (cities.isNotEmpty()) cities[0] else "No City Selected"
                            userViewModel.updateLastSelectedCity(selectedCity)
                            }
                        ) {
                            Text("Delete City")
                        }
                    }

                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Places in ${selectedCity.split(",").first().trim()}")

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .verticalScroll(state = rememberScrollState(), enabled = true)
                            .align(Alignment.Center)
                            .fillMaxWidth()
                    ) {
                        if (entriesToDisplay.isNotEmpty()) {
                            entriesToDisplay.forEach { entry ->
                                EntryCardFragment(
                                    navController = navController,
                                    entry = entry,
                                    index = null,
                                    height = screenWidth - 60.dp,
                                    modifier = Modifier.width(screenWidth - 50.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }
        }
        if (addCityModalVisible) {
            AddCityModal(viewModel = mapViewModel, onCloseClicked = onCloseClicked)
        }
    }
}

@Composable
fun AddCityModal(onCloseClicked: (city: String) -> Unit, viewModel: MapViewModel) {
    var text by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.5f))
            .padding(16.dp)
            .clickable(onClick = {}),
        contentAlignment = Alignment.Center
    ) {
        Surface (
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            Column (
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add a New City",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            viewModel.searchCities(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    AnimatedVisibility(
                        viewModel.locationAutofill.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            itemsIndexed(viewModel.locationAutofill) { _, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp)
                                        .clickable {
                                            text = item.address
                                            viewModel.locationAutofill.clear()
                                        }
                                ) {
                                    Text(text = item.address)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { onCloseClicked(text) }) {
                    Text(text = "Add ${text.split(",").first().trim()}")
                }
            }
        }

    }
}
