package com.group22.cityspots.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.group22.cityspots.viewmodel.MapViewModel

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    close: () -> Unit
) {
    val uiSettings = remember{MapUiSettings()}
    Scaffold { innerPadding ->
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            properties = viewModel.state.properties,
            uiSettings = uiSettings,
            onMapLongClick = {

            }
        )
        Box(
            contentAlignment = Alignment.TopStart
        ) {
            Button(
                onClick = close,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                modifier = Modifier.padding(5.dp),
                contentPadding = PaddingValues(6.dp)
            ) {
                Text(text = "Back")
            }
        }
    }
}