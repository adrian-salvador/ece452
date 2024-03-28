package com.group22.cityspots.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.group22.cityspots.respository.Firestore
import com.group22.cityspots.viewmodel.AddEntryViewModel
import com.group22.cityspots.viewmodel.AddEntryViewModelFactory
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
    var newTag by remember { mutableStateOf("") }
    val tags = remember { mutableStateListOf("city", "nature") }
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        Column {
            Spacer(modifier = Modifier.height(20.dp))
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
                    .padding(horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .height(30.dp)
            ) {
                tags.forEachIndexed { index, tag ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFDBE8F9),
                        contentColor = Color(0xFF176FF2),
                        modifier = Modifier.clickable {
                            tags.removeAt(index)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(start = 10.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                        ){
                            Text(
                                text = tag,
                                style = TextStyle(fontWeight = FontWeight.Bold),
                            )
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                            )
                        }
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                entries?.forEachIndexed() { index, entry ->
                    item {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()

                                .height(200.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xffd5d4d7))
                                .clickable {
                                    navController.navigate("entry/${entry.entryId}")
                                }
                        ) {
                            if (entry.pictures?.isNotEmpty() == true) {
                                AsyncImage(
                                    model = entry.pictures.first(),
                                    contentDescription = "Image of entry",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .clip(RoundedCornerShape(20.dp)),
                                    contentScale = ContentScale.FillBounds
                                )
                            } else {
                                Icon(
                                    Icons.Filled.Place,
                                    contentDescription = "No image available",
                                    modifier = Modifier.size(50.dp),
                                    tint = Color.Gray
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "${index + 1}.",
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(
                                            Color.Black.copy(alpha = 0.5f),
                                            RoundedCornerShape(15.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = entry.title,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White,
                                    modifier = Modifier
                                        .background(
                                            Color.Black.copy(alpha = 0.5f),
                                            RoundedCornerShape(15.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                Row(
                                    modifier = Modifier
                                        .background(
                                            Color.Black.copy(alpha = 0.5f),
                                            RoundedCornerShape(15.dp)
                                        )
                                        .padding(horizontal = 5.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically

                                ) {
                                    Icon(
                                        Icons.Filled.Star,
                                        contentDescription = "No image available",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color(0xfff8d675)
                                    )
                                    Text(
                                        text = String.format("%.2f", entry.rating),
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}