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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.group22.cityspots.viewmodel.RankingScreenViewModel
import com.group22.cityspots.viewmodel.RankingScreenViewModelFactory
import com.group22.cityspots.viewmodel.UserViewModel

@Composable
fun RankingScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.userLiveData.observeAsState()
    val rankingScreenViewModel: RankingScreenViewModel = viewModel(
        factory = RankingScreenViewModelFactory(user!!.userId)
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
            Text(text = "Rankings", fontWeight = FontWeight.Medium, fontSize = 36.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier
                .fillMaxWidth()
                ) {
                Row(
                    modifier = Modifier
                        .clickable { expanded = true }
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF3F8FE))
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedTrip?.title ?: "Select a trip",
                        modifier = Modifier
                            .padding(start = 15.dp),
                        color = if (selectedTrip == null) Color.LightGray else Color.Black
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }

                // Dropdown menu
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(Color(0xFFF3F8FE))
                        .width(350.dp)
                ) {
                    DropdownMenuItem(
                        onClick = {
                            rankingScreenViewModel.setSelectedTrip(null)
                            expanded = false
                        },
                        text = { Text("None") }
                    )

                    if (trips.isNotEmpty()){
                        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 15.dp))
                    }

                    trips.forEachIndexed { index, trip ->
                        DropdownMenuItem(
                            onClick = {
                                rankingScreenViewModel.setSelectedTrip(trip)
                                expanded = false
                            },
                            text = { Text(trip.title) }
                        )
                        if (index < trips.size - 1) {
                            HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(horizontal = 15.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                        EntryCardFragment(
                            navController = navController,
                            entry = entry,
                            index = index,
                            height = 200.dp,
                            modifier = Modifier.weight(1f)
                        )
                        if (rowEntries.size < 2) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
