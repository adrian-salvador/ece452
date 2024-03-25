package com.group22.cityspots.view

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.group22.cityspots.model.Entry
import com.group22.cityspots.viewmodel.AddEntryViewModel

@Composable
fun RatingSelectionPopup(addEntryViewModel: AddEntryViewModel, onClose: () -> Unit) {
    val rating by addEntryViewModel.ratingLiveData.observeAsState(initial = -1.0)

    val (previousEntry, nextEntry) = addEntryViewModel.getAdjacentEntriesForRating(rating)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            EntryColumn(entry = previousEntry, defaultText = "Highest Rating")
            Spacer(modifier = Modifier.height(16.dp))

            // Display Current Rating
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Current Rating", style = MaterialTheme.typography.bodyMedium)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp) 
                        .border(border = BorderStroke(2.dp, Color.LightGray), shape = CircleShape)
                        .padding(top = 4.dp)
                ) {
                    Text(String.format("%.2f", rating), style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Display Next Entry or "Lowest Rating" if null
            EntryColumn(entry = nextEntry, defaultText = "Lowest Rating")
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Slider(
                value = rating.toFloat(),
                onValueChange = { newRating ->
                    addEntryViewModel.updateRating(newRating.toDouble())
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

@Composable
fun EntryColumn(entry: Entry?, defaultText: String) {
    val textToShow = entry?.title ?: ""

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(textToShow, style = MaterialTheme.typography.bodyMedium)
        EntryCircle(entry = entry, defaultText = defaultText)
    }
}

@Composable
fun EntryCircle(entry: Entry?, defaultText: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(100.dp) 
            .border(border = BorderStroke(2.dp, Color.LightGray), shape = CircleShape)
            .padding(top = 4.dp)
    ) {
        entry?.pictures?.firstOrNull()?.let { imageUrl ->
            val painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current).data(data = imageUrl)
                    .apply<ImageRequest.Builder>(block = fun ImageRequest.Builder.() {
                        transformations(CircleCropTransformation())
                    }).build()
            )
            Image(
                painter = painter,
                contentDescription = "Cropped Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop 
            )
        } ?: Text(defaultText, style = MaterialTheme.typography.bodyMedium)
    }
}

