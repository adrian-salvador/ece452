package com.group22.cityspots.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.group22.cityspots.viewmodel.RankingScreenViewModel
import com.group22.cityspots.viewmodel.RankingScreenViewModelFactory
import com.group22.cityspots.viewmodel.UserViewModel

@Composable
fun FriendRankingScreen(
    navController: NavController,
    backStackEntry: NavBackStackEntry,
    userViewModel: UserViewModel
) {
    val userId = backStackEntry.arguments!!.getString("userId")
    val username = backStackEntry.arguments!!.getString("userName")
    val user by userViewModel.userLiveData.observeAsState()
    val rankingScreenViewModel: RankingScreenViewModel = viewModel(
        factory = RankingScreenViewModelFactory(userId!!)
    )
    val entries by rankingScreenViewModel.entriesLiveData.observeAsState()
    val tags = rankingScreenViewModel.tags.observeAsState()
    var expanded by remember { mutableStateOf(false) }
    val trips by rankingScreenViewModel.tripsLiveData.observeAsState(listOf())
    val selectedTrip by rankingScreenViewModel.selectedTrip.observeAsState()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.titleMedium,
                text = "${username!!}'s Rankings"
            )
            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(onClick = { expanded = true })
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Text field to display the selected trip's title
                        Text(
                            text = selectedTrip?.title ?: "Select a trip",
                            modifier = Modifier
                                .clickable { expanded = true }
                                .background(Color.White)
                                .padding(start = 48.dp, end = 136.dp),
                            color = Color.Black
                        )

                        // Dropdown menu
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            DropdownMenuItem(
                                modifier = Modifier
                                    .padding(start = 48.dp, end = 136.dp)
                                    .background(Color.White),
                                text = { Text("All") },
                                onClick = {
                                    rankingScreenViewModel.setSelectedTrip(null)
                                    expanded = false
                                }
                            )
                            trips.forEach { trip ->
                                DropdownMenuItem(
                                    text = { Text(trip.title) },
                                    onClick = {
                                        rankingScreenViewModel.setSelectedTrip(trip)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            TagEditorFragment(
                tags = tags,
                addTag = { tag -> rankingScreenViewModel.addTag(tag) },
                removeTag = { tag -> rankingScreenViewModel.removeTag(tag) }
            )
            Spacer(modifier = Modifier.height(10.dp))
            val chunkedEntries = entries?.chunked(2) ?: listOf()
            chunkedEntries.forEachIndexed { rowIndex, rowEntries ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    rowEntries.forEachIndexed { columnIndex, entry ->
                        val index = rowIndex * 2 + columnIndex
                        EntryCardFragment(navController = navController, entry = entry, index = index, height = 200.dp, modifier = Modifier.weight(1f) )
                        if (rowEntries.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}