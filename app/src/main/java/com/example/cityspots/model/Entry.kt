package com.example.cityspots.model

data class Entry(
    val id: Int,
    val content: String,
    val pictures: List<String>, // Assuming pictures are stored as URLs or file paths
    val ranking: Int, // Assuming ranking is a numerical value, adjust type if needed
    val review: String, // Simple text review, consider a complex type for more structured data
    val tags: List<String>, // List of tags associated with the entry
    val geoLocation: GeoLocation // Using a custom data class to hold latitude and longitude
)
