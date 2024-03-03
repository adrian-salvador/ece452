package com.example.cityspots.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.navigation.NavController
import com.example.cityspots.model.Entry
import com.example.cityspots.model.GeoLocation
import com.example.cityspots.viewmodel.UserViewModel


@Composable
fun AddEntryScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.userLiveData.observeAsState()

    val ranking: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    var entryName by remember { mutableStateOf("") }
    var hasTitle by remember { mutableStateOf(false) }

    var rankingExpanded by remember { mutableStateOf(false) }
    var selectedRanking by remember { mutableIntStateOf(ranking[0]) }

    var rating by remember { mutableIntStateOf(0) }

    var description by remember { mutableStateOf("") }

    val tags = remember { mutableStateListOf("city", "nature") }
    var newTag by remember { mutableStateOf("") }
    var tagBoxHasTag by remember { mutableStateOf(false) }

    Scaffold (
        bottomBar = { BottomNavigationBar(navController = navController) },
        containerColor = Color(0x2F84ABE4)
    ){ innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.padding(18.dp)
            ) {
                // Location Name and Ranking
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                            onValueChange = {
                                entryName = it
                                hasTitle = it.isNotEmpty()
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            textStyle = MaterialTheme.typography.titleLarge
                        )
                    }

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
                                        text = "#$selectedRanking",
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
                            ranking.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item.toString()) },
                                    onClick = {
                                        selectedRanking = item
                                        rankingExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Rating
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.padding(bottom = 15.dp)
                ) {
                    RatingBar(
                        rating = rating,
                        onRatingChanged = { newRating -> rating = newRating })
                }

                // Description
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, bottom = 12.dp)
                ) {
                    TextField(
                        value = description,
                        onValueChange = { description = it },
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

                // Tags
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

                // Submit Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)
                ) {
                    Button(onClick = {
                        user?.entries?.add(
                            Entry(
                                id = (user?.entries?.maxOfOrNull { it.id } ?: 0 ) + 1,
                                content = entryName,
                                pictures = listOf("https://example.com/pic11.jpg", "https://example.com/pic12.jpg"),
                                ranking = selectedRanking,
                                review = description,
                                tags = tags,
                                geoLocation = GeoLocation(0.0, 0.0)
                            )
                        )
                        navController.popBackStack()
                    }) {
                        Text("Add Entry")
                    }
                }
            }
        }
    }
//    Spacer(modifier = Modifier.height(8.dp))
//    Button(onClick = {
//        // val newEntry = Entry(id = (user.entries.maxOfOrNull { it.id } ?: 0) + 1, content = text)
//        // user.entries.add(newEntry)
//        navController.popBackStack()
//    }) {
//        Text("Add")
//    }
}

@Composable
fun RatingBar(rating: Int, onRatingChanged: (Int) -> Unit) {
    Row {
        repeat(5) {
            val starColor = if (it < rating) Color(0xFFFFA100) else Color.Gray
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = starColor,
                modifier = Modifier
                    .clickable { onRatingChanged(it + 1) }
            )
        }
    }
}
