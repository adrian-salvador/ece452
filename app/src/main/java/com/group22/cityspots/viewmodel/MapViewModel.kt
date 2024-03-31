package com.group22.cityspots.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class AutocompleteResult(
    val address: String,
    val placeId: String
)
class MapViewModel(context: Context) : ViewModel() {
    var state by mutableStateOf(MapState())
    val locationAutofill = mutableStateListOf<AutocompleteResult>()
    private val placesClient: PlacesClient = Places.createClient(context)
    var currentLatLong by mutableStateOf(LatLng(43.4668, -80.51639))
    var currentPlace by mutableStateOf("Select Location")
    var currentPlaceId by mutableStateOf("")
    var currentAddress by mutableStateOf("")
    private var job: Job? = null

    fun searchPlaces(query: String) {
        job?.cancel()
        locationAutofill.clear()
        job = viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest
                .builder()
                .setQuery(query)
                .build()
            placesClient
                .findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    locationAutofill += response.autocompletePredictions.map {
                        AutocompleteResult(
                            it.getFullText(null).toString(),
                            it.placeId
                        )
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    println(it.cause)
                    println(it.message)
                }
        }
    }
    fun getCoordinates(result: AutocompleteResult) {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(result.placeId, placeFields)
        val addressArray = result.address.split(',')
        currentPlace = addressArray[0]
        currentAddress = addressArray[addressArray.size - 3] + ',' +
                         addressArray[addressArray.size - 2] + ',' +
                         addressArray[addressArray.size - 1]
        currentPlaceId = result.placeId
        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                if (it != null) {
                    currentLatLong = it.place.latLng!!
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun updateLocation(newPlace: String, newAddress: String){
        currentPlaceId = newPlace
        currentAddress = newAddress
    }
}