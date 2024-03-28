package com.group22.cityspots.view

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.HorizontalAlignmentLine
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.group22.cityspots.model.Entry
import com.group22.cityspots.viewmodel.AddEntryViewModel
import kotlinx.coroutines.launch
import kotlin.math.round
import kotlin.math.roundToInt

@Composable
fun RatingSelectionPopup(addEntryViewModel: AddEntryViewModel, onClose: () -> Unit) {
    val rating by addEntryViewModel.ratingLiveData.observeAsState(initial = -1.0)
    val entries by addEntryViewModel.entriesLiveData.observeAsState(listOf(Entry()))

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    fun scrollToPosition(position: Int) {
        coroutineScope.launch {
            scrollState.animateScrollTo(position)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {

        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            // Display Current Rating
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Current Rating", style = MaterialTheme.typography.bodyMedium)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(50.dp)
                        .border(border = BorderStroke(2.dp, Color.LightGray), shape = CircleShape)
                        .padding(top = 4.dp)
                ) {
                    Column {
                        Text(
                            String.format("%.2f", rating),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .padding(horizontal = 25.dp)
                    .horizontalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.width(160.dp))
                entries.forEach{ entry ->
                    EntryCardFragment(
                        navController = null,
                        entry = entry,
                        index = null ,
                        height = 150.dp,
                        modifier = Modifier.width(100.dp)
                    )
                }
                Spacer(modifier = Modifier.width(160.dp))
            }
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Slider(
                value = rating.toFloat(),
                onValueChange = { newRating ->
                    val entriesRowLength = entries.size*120
                    val newRatingDouble = newRating.toDouble()
                    addEntryViewModel.updateRating(newRatingDouble)
                    val (prevIndex, nextIndex) = addEntryViewModel.getAdjacentEntriesIndicesForRating(newRatingDouble)
                    println("prev: ${prevIndex}, next: ${nextIndex}")
                    when {
                        prevIndex == null -> scrollToPosition(0)
                        nextIndex == null -> scrollToPosition( entriesRowLength+ 160)
                        else -> {
                            // Both prevIndex and nextIndex are non-null
                            val prevRating = entries[prevIndex].rating
                            val nextRating = entries[nextIndex].rating
                            println("prevRating: ${prevRating}, nextRating: ${nextRating}")
                            val baseScroll = (prevIndex) * 330 // Base scroll position based on prevIndex

                            // Calculate additional scroll based on the rating difference
                            val ratingPerPixel = (nextRating - prevRating) / 220.0
                            val additionalScroll = ((newRatingDouble - prevRating) / ratingPerPixel).roundToInt()

                            scrollToPosition(baseScroll + additionalScroll)
                            println("BaseScroll: ${baseScroll} Scroll: ${baseScroll + additionalScroll}")
                        }
                    }


                },
                valueRange = 0f..5f,
                steps = 0
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onClose) {
                Text("Done")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

