package com.example.cityspots.view

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cityspots.model.Entry

@Composable

fun EntryScreen(navController: NavController, currentEntry : Entry) {

    Scaffold (
        bottomBar = { BottomNavigationBar(navController = navController) },
        containerColor = Color(0x2F84ABE4)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column (
                Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {

                Spacer(modifier = Modifier.height(20.dp))

                Row (
                    Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                    ,
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (image in currentEntry.pictures) {
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
                        Column {
                            Text(
                                modifier = Modifier.padding(start = 20.dp),
                                style = MaterialTheme.typography.titleLarge,
                                text = currentEntry.content,
                                color = Color.Black,
                            )
                            Text(
                                text = "${currentEntry.geoLocation.longitude}°, " +
                                        "${currentEntry.geoLocation.latitude}°",
                                modifier = Modifier.padding(start = 20.dp),
                                color = Color.Gray,
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

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
                                text = "#${currentEntry.ranking}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .background(Color.White, CircleShape)
                                    .padding(9.dp)
                            )
                        }
                    }

                    Spacer(Modifier.padding(end = 20.dp))
                }

                Spacer(modifier = Modifier.height(30.dp))

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
                            .background(Color.White)
                            .padding(20.dp)
                    ) {
                        Text(text = currentEntry.review)
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    modifier = Modifier.padding(start = 20.dp),
                    style = MaterialTheme.typography.titleMedium,
                    text = "Tags"
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row (
                    Modifier
                        .horizontalScroll(rememberScrollState())
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                    ,
                    horizontalArrangement = Arrangement.Center
                ){
                    for (tag in currentEntry.tags) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(40.dp))
                                .background(Color.White)
                                .padding(15.dp)
                        ) {
                            Text(
                                text = tag,
                                color = Color(0xFF176FF2),
                            )
                        }
                        Spacer(Modifier.padding(end = 15.dp))
                    }
                }
            }
        }
    }
}