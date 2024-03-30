package com.group22.cityspots.model

data class Entry(
    val entryId: String? = null,
    val title: String = "",
    val pictures: List<String>? = null,
    val review: String = "",
    val tags: List<String> = emptyList(),
    val geoLocation: GeoLocation = GeoLocation(0.0, 0.0),
    val rating: Double = 0.0,
    val userId: String = "",
    val tripId: String = ""
)

