package com.group22.cityspots.view

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.group22.cityspots.model.Entry
import com.group22.cityspots.model.GeoLocation
import com.group22.cityspots.viewmodel.AddEntryViewModel
import com.group22.cityspots.viewmodel.AddEntryViewModelFactory
import com.group22.cityspots.viewmodel.UserViewModel


@Composable
fun AddEntryScreen(navController: NavController, userViewModel: UserViewModel) {
    val user by userViewModel.userLiveData.observeAsState()
    var entryName by remember { mutableStateOf("") }
    var hasTitle by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    val addEntryViewModel: AddEntryViewModel = viewModel(
        factory = AddEntryViewModelFactory(user!!.userId)
    )
    val tags = addEntryViewModel.tags.observeAsState(listOf())
    val rating by addEntryViewModel.ratingLiveData.observeAsState(0.00)
    var showRatingPopup by remember { mutableStateOf(false) }

    val imageUris = remember { mutableStateListOf<Uri>() }
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { imageUris.add(it) }
        }
    )

    val context = LocalContext.current


    Box(modifier = Modifier
        .background(Color(0x2F84ABE4))
        .padding(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(18.dp).verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LocationNameEntry(entryName, hasTitle, Modifier.weight(1F)) { name, hasName ->
                    entryName = name
                    hasTitle = hasName
                }

                Button(
                    onClick = { showRatingPopup = true },
                    modifier = Modifier.wrapContentWidth().widthIn(max = 150.dp)
                ) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "No image available",
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xfff8d675)
                    )
                    Text(String.format("%.2f", rating), style = MaterialTheme.typography.bodyLarge)

                }
            }

            DisplayLocation()

            DescriptionEntry(description) { newDescription ->
                description = newDescription
            }

            TagEditorFragment(
                tags = tags,
                addTag = { tag -> addEntryViewModel.addTag(tag) },
                removeTag = { tag -> addEntryViewModel.removeTag(tag) }
            )

            // Display Selected Images
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 10.dp)
            ) {
                imageUris.forEach { uri ->
                    CroppedSquareImage(uri)
                }
            }

            //Upload Image Button
            Button(
                onClick = { pickImagesLauncher.launch("image/*") },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text("Upload Images")
            }

            //Submit Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp)
            ) {
                Button(onClick = {
                    if (hasTitle) {
                        val entryDetails = Entry(
                            entryId = null,
                            title = entryName,
                            pictures = null,
                            review = description,
                            tags = tags.value,
                            geoLocation = GeoLocation(0.0, 0.0),
                            rating = rating.toDouble(),
                            userId = user!!.userId
                        )
                        addEntryViewModel.uploadImagesAndCreateEntry(imageUris, entryDetails, context )
                        navController.popBackStack()
                    }else {
                        Toast.makeText(context, "Please add an Activity Name", Toast.LENGTH_LONG).show()
                    }
                }) {
                    Text("Add Entry")
                }
            }
        }
    }

    if (showRatingPopup) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = { showRatingPopup = false }),
            contentAlignment = Alignment.Center
        ) {
            RatingSelectionPopup(addEntryViewModel) {
                showRatingPopup = false
            }
        }
    }
}

@Composable
fun CroppedSquareImage(imageUri: Uri) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = imageUri)
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
}

@Composable
fun LocationNameEntry(entryName: String, hasTitle: Boolean, modifier: Modifier, onValueChange: (String, Boolean) -> Unit) {
    Box (modifier = modifier){
        if (!hasTitle) {
            Text(
                text = "Activity Name",
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
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )
    }
}

@Composable
fun TagEntry(tags: MutableList<String>) {
    var newTag by remember { mutableStateOf("") }

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
    )
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
                    text = "New York City",
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