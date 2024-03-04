package com.example.cityspots.model

data class User(
    val id: Int,
    val name: String,
    var rankings: RankingList,
    var total_entry_count: Int,
    var friends: MutableList<Friend>
)