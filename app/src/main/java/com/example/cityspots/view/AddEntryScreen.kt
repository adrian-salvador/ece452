package com.example.cityspots.view

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cityspots.model.Entry
import com.example.cityspots.model.GeoLocation
import com.example.cityspots.model.RankingList
import com.example.cityspots.viewmodel.AddEntryScreenViewModel
import com.example.cityspots.viewmodel.AddEntryScreenViewModelFactory
import com.example.cityspots.viewmodel.UserViewModel


@Composable
fun AddEntryScreen(navController: NavController, userViewModel: UserViewModel) {
    val addEntryViewModel: AddEntryScreenViewModel = viewModel(
        factory = AddEntryScreenViewModelFactory(userViewModel)
    )

    val newEntryId = addEntryViewModel.newEntryId
    val clonedRankings by addEntryViewModel.clonedRankings.observeAsState()
    val newEntryRanking by addEntryViewModel.newEntryRanking.observeAsState()

    var entryName by remember { mutableStateOf("") }
    var hasTitle by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    val tags = remember { mutableStateListOf("city", "nature") }

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        containerColor = Color(0x2F84ABE4)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LocationNameEntry(entryName, hasTitle) { name, hasName ->
                        entryName = name
                        hasTitle = hasName
                    }

                    RankingDropdown(
                        addEntryViewModel,
                        newEntryRanking,
                        newEntryId,
                        clonedRankings,
                        hasTitle,
                        entryName,
                        description,
                        tags
                    )
                }

                DisplayLocation()

                DescriptionEntry(description) { newDescription ->
                    description = newDescription
                }

                TagEntry(tags)
                DisplayTags(tags)

                SubmitButton {
                    if (hasTitle) {
                        val newEntry = Entry(
                            id = newEntryId,
                            title = entryName,
                            pictures = listOf(),
                            review = description,
                            tags = tags,
                            geoLocation = GeoLocation(0.0, 0.0)
                        )
                        addEntryViewModel.insertEntryInClone(newEntryRanking ?: 0, newEntry)
                        addEntryViewModel.commitClonedRankingsToUser()
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

@Composable
fun LocationNameEntry(entryName: String, hasTitle: Boolean, onValueChange: (String, Boolean) -> Unit) {
    Box {
        if (!hasTitle) {
            Text(
                text = "Location Name",
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
fun RankingDropdown(
    addEntryViewModel: AddEntryScreenViewModel,
    newEntryRank: Int?,
    newEntryId: Int,
    clonedRankings: RankingList?,
    hasTitle: Boolean,
    entryName: String,
    description: String,
    tags: MutableList<String>
) {
    var rankingExpanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { rankingExpanded = !rankingExpanded }
        ) {
            Card(
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                modifier = Modifier.size(50.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = "%.2f".format((1-(newEntryRank!!).toFloat() / (clonedRankings!!.length()-1)) * 5),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .background(Color.White, CircleShape)
                            .padding(9.dp)
                    )
                }
            }
            Icon(
                Icons.Default.ArrowDropDown,
                contentDescription = "dropdown"
            )
        }
        DropdownMenu(
            expanded = rankingExpanded,
            onDismissRequest = { rankingExpanded = false }) {
            clonedRankings?.toList()?.forEachIndexed { index, entry ->
                DropdownMenuItem(
                    text = { Text("#${index + 1}: ${entry.title}") },
                    onClick = {
                        rankingExpanded = false
                        addEntryViewModel.insertEntryInClone(index, Entry(
                            id = newEntryId,
                            title = if (hasTitle) entryName else "New Entry",
                            pictures = listOf(), // Add picture URLs if necessary
                            review = description,
                            tags = tags.toList(),
                            geoLocation = GeoLocation(0.0, 0.0) // Set appropriate geolocation
                        ))
                    }
                )
            }
        }
    }
}



@Composable
fun DescriptionEntry(description: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, bottom = 12.dp)
    ) {
        TextField(
            value = description,
            onValueChange = onValueChange,
            placeholder = { Text("Description") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color(0xFF84ABE4),
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}

@Composable
fun TagEntry(tags: MutableList<String>) {
    var newTag by remember { mutableStateOf("") }
    var tagBoxHasTag by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart
        ) {
            TextField(
                value = newTag,
                onValueChange = {
                    newTag = it
                    tagBoxHasTag = it.isNotEmpty()
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (newTag.isNotBlank()) {
                        tags.add(newTag)
                        newTag = ""
                        tagBoxHasTag = false
                    }
                }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color(0xFFF3F8FE),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(40.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
            if (!tagBoxHasTag) {
                Text(
                    text = "Add Tags...",
                    color = Color(0xFF176FF2),
                    style = TextStyle(fontStyle = FontStyle.Italic, fontSize = 16.sp),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
fun DisplayLocation() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                Text(
                    text = "Waterloo",
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


@Composable
fun RatingBar(rating: Float) {
    println(rating)
    Box {
        // Stars
        Row {
            repeat(5) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Star $it",
                    tint = Color(0xFFFFA100),
                )
            }
        }

        // Covering Rectangle
        Box(
            Modifier
                .matchParentSize() // Match the size of the Box
                .background(Color.LightGray.copy(alpha = 0.5f)) // Slightly transparent
                .offset(x = (50 * rating).dp) // Move the rectangle based on the rating
        )
    }
}




@Composable
fun SubmitButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
    ) {
        Button(onClick = onClick) {
            Text("Add Entry")
        }
    }
}