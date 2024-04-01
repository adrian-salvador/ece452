package com.group22.cityspots.model

data class Friends (
    val userId: String? = null,
    val friendId: String? = null,
    var friendIDs: MutableList<String>? = mutableListOf(),
    var sentRequests: MutableList<String>? = mutableListOf(),
    val recvRequests: MutableList<String>? = mutableListOf(),
)
