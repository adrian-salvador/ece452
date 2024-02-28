package com.example.cityspots.model

data class User(
    val id: String,
    val name: String,
    var entries: MutableList<Entry>
)