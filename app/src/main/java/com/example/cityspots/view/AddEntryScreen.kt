package com.example.cityspots.view

import android.graphics.drawable.Icon
import android.view.RoundedCorner
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight


@Composable
fun AddEntryScreen(navController: NavController) {
    val ranking: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    val tags: List<String> = listOf("city", "nature")

    var entryName by remember { mutableStateOf("") }
    var rankingExpanded by remember { mutableStateOf(false) }
    var selectedRanking by remember { mutableStateOf(ranking[0]) }
    var rating by remember { mutableStateOf(0) }
    var description by remember { mutableStateOf("") }
    var selectedTags by remember { mutableStateOf(mutableListOf<String>()) }
    var tagsExpanded by remember { mutableStateOf(false) }

    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        // Location Name and Ranking
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = entryName,
                onValueChange = { entryName = it },
                label = { Text("Location Name") }
            )

            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rankingExpanded = !rankingExpanded }
                ) {
                    Text(
                        text = "#$selectedRanking",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "dropdown"
                    )
                }
                DropdownMenu(expanded = rankingExpanded, onDismissRequest = { rankingExpanded = false }) {
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
            RatingBar(rating = rating, onRatingChanged = {newRating -> rating = newRating})
        }

        // Description
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        // Tags
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.clickable { tagsExpanded = !tagsExpanded }
            ) {
                selectedTags.forEach { tag ->
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.clickable( onClick = {selectedTags.remove(tag)} )
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            tags.forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF92BEFF),
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
