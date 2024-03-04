package com.example.cityspots.model

data class User(
    val id: Int,
    val name: String,
    var entries: MutableList<Entry>,
    var friends: MutableList<Friend>
)