package com.group22.cityspots.model

data class Entry(
    val id: Int,
    val title: String,
    val pictures: List<String>,
    val review: String,
    val tags: List<String>,
    val geoLocation: GeoLocation
)
