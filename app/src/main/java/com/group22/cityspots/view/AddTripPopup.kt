package com.group22.cityspots.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group22.cityspots.model.Trip
import com.group22.cityspots.viewmodel.AddTripViewModel
import com.group22.cityspots.viewmodel.AddTripViewModelFactory

@Composable
fun AddTripPopup(userId: String, onClose: () -> Unit) {
    val addTripViewModel: AddTripViewModel = viewModel(
        factory = AddTripViewModelFactory(userId)
    )
    var tripName by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .padding(10.dp)
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Trip Name:",
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = tripName,
                onValueChange = { tripName = it },
                placeholder = { Text("Enter Trip Name") },
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (tripName.isNotEmpty()) {
                        val tripDetails = Trip(
                            tripId = null,
                            title = tripName,
                            userId = userId
                        )
                        addTripViewModel.createTrip(tripDetails, context)
                        onClose()
                    }else {
                        Toast.makeText(context, "Please add a Trip Name", Toast.LENGTH_LONG).show()
                    }
                          },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("Add Trip")
            }
        }
    }
}
