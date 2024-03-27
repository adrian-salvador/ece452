package com.group22.cityspots.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.setValue

class MapViewModel : ViewModel() {
    var state by mutableStateOf(MapState())
}