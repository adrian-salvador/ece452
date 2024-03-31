package com.group22.cityspots.model

data class Entry(
    val entryId: String? = null,
    val title: String = "",
    val pictures: List<String>? = null,
    val review: String = "",
    val tags: List<String> = emptyList(),
    val placeId: String = "",
    val address: String = "",
    val place: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var rating: Double = 0.0,
    val userId: String = "",
    val tripId: String = ""
)

