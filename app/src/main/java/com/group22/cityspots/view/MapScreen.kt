package com.group22.cityspots.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.group22.cityspots.viewmodel.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    close: () -> Unit
) {
    val uiSettings = remember{MapUiSettings()}
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(
            viewModel.currentLatLong,
            13f
        )
    }
    var searchBarFocused by remember { mutableStateOf(false) }
    LaunchedEffect(viewModel.currentLatLong) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLng(viewModel.currentLatLong))
    }
    Scaffold { innerPadding ->
        GoogleMap(
            cameraPositionState = cameraPositionState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            properties = viewModel.state.properties,
            uiSettings = uiSettings,
        ) {
            Marker(
                state = MarkerState(viewModel.currentLatLong),
                draggable = false,
                title = viewModel.currentPlace
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                color = Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 10.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    var text by remember { mutableStateOf("") }
                    TextField(
                        value = text,
                        label = { Text(
                            "Search for a location",
                            style = TextStyle(
                                fontSize = if (searchBarFocused) 12.sp else 14.sp
                            )
                        ) },
                        textStyle = TextStyle(
                            fontSize = 14.sp
                        ),
                        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent),
                        onValueChange = {
                            text = it
                            viewModel.searchPlaces(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged {
                                searchBarFocused = it.isFocused
                            }
                    )
                    AnimatedVisibility(
                        viewModel.locationAutofill.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            itemsIndexed(viewModel.locationAutofill) { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .clickable {
                                            text = item.address
                                            viewModel.locationAutofill.clear()
                                            viewModel.getCoordinates(item)
                                        }
                                ) {
                                    Text(item.address)
                                }
                            }
                        }
                    }
                }
            }
            Button(
                onClick = close,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .align(Alignment.BottomCenter),
                contentPadding = PaddingValues(6.dp)
            ) {
                Icon(Icons.Filled.Check, "Save Location")
            }
        }
    }
}