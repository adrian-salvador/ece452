package com.group22.cityspots.view

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group22.cityspots.model.Entry
import com.group22.cityspots.viewmodel.AddEntryViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun RatingSelectionPopup(addEntryViewModel: AddEntryViewModel, onClose: () -> Unit) {
    val rating by addEntryViewModel.ratingLiveData.observeAsState(initial = 0.00)
    val entries by addEntryViewModel.entriesLiveData.observeAsState(listOf(Entry()))

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    fun scrollToPosition(position: Int) {
        coroutineScope.launch {
            scrollState.animateScrollTo(position)
        }
    }

    fun scrollBasedOnRating(newRating: Double) {
        val sizeOfEntry = 330
        val entriesRowLength = entries.size * 330
        addEntryViewModel.updateRating(newRating)
        val (prevIndex, nextIndex) = addEntryViewModel.getAdjacentEntriesIndicesForRating(newRating)
        println("prev: ${prevIndex}, next: ${nextIndex}")
        when {
            prevIndex == null || newRating == 0.0 -> {
                if (entries.any { it.rating == 0.0 }) {
                    scrollToPosition(160)
                } else {
                    scrollToPosition(0)
                }
            }

            nextIndex == null -> {
                if (entries.any { it.rating == 5.0 }) {
                    scrollToPosition(entriesRowLength - sizeOfEntry + 160)
                } else {
                    scrollToPosition(entriesRowLength + 160)
                }
            }

            else -> {
                // Both prevIndex and nextIndex are non-null
                val prevRating = entries[prevIndex].rating
                val nextRating = entries[nextIndex].rating
                println("prevRating: ${prevRating}, nextRating: ${nextRating}")
                val baseScroll =
                    (prevIndex) * sizeOfEntry + 160 // Base scroll position based on prevIndex

                // Calculate additional scroll based on the rating difference
                val pixelPerRating = sizeOfEntry / (nextRating - prevRating)
                val additionalScroll = ((newRating - prevRating) * pixelPerRating).roundToInt()

                scrollToPosition(baseScroll + additionalScroll)
                println("BaseScroll: ${baseScroll} Extra: ${additionalScroll} Pix/Rating: $pixelPerRating")
            }
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Display Current Rating
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    //Text("Entry Rating:", fontSize = 30.sp)
                    Row(
                        modifier = Modifier.wrapContentWidth().widthIn(max = 150.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = "No image available",
                            modifier = Modifier.size(30.dp),
                            tint = Color(0xfff8d675)
                        )
                        Text(String.format("%.2f", rating), fontSize = 30.sp, modifier = Modifier.width(80.dp))

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
                        //.padding(horizontal = 25.dp)
                        .horizontalScroll(scrollState)
                        .onSizeChanged { newSize -> println("Width: ${newSize.width}") }
                ) {
                    Spacer(modifier = Modifier.width(185.dp))
                    entries.forEach { entry ->
                        EntryCardFragment(
                            navController = null,
                            entry = entry,
                            index = null,
                            height = 150.dp,
                            modifier = Modifier.width(100.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(185.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Slider(
                    value = rating.toFloat(),
                    onValueChange = { newRating ->
                        scrollBasedOnRating(newRating.toDouble())
                    },
                    valueRange = 0f..5f,
                    steps = 0,
                    modifier = Modifier.width(300.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = onClose) {
                    Text("Done")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        scrollBasedOnRating(rating)
    }
}

