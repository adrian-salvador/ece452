package com.group22.cityspots.view

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.group22.cityspots.viewmodel.MapViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = MapViewModel(LocalContext.current),
    selectedLocation: MutableState<LatLng?>,
    close: () -> Unit
) {
    val uiSettings = remember{MapUiSettings()}
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(
            selectedLocation.value ?: LatLng(43.4668, -80.51639),
            15f
        )
    }
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
            onMapClick = {
                selectedLocation.value = it
            }
        ) {
            selectedLocation.value?.let {
                Marker(
                    state = MarkerState(it),
                    draggable = true,
                    title = "Activity Location",
                    snippet = "${selectedLocation.value?.longitude}, ${selectedLocation.value?.latitude}"
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                    .fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var text by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = text,
                        onValueChange = {
                            text = it
                            viewModel.searchPlaces(it)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
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
                    .padding(5.dp)
                    .align(Alignment.BottomStart),
                contentPadding = PaddingValues(6.dp)
            ) {
                Text(text = "Back")
            }
        }
    }
}