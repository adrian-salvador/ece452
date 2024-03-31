package com.group22.cityspots.model

data class Friends (
    val userId: String? = null,
    val friendIDs: MutableList<String>? = mutableListOf(),
    val sentRequests: MutableList<String>? = mutableListOf(),
    val recvRequests: MutableList<String>? = mutableListOf(),
)
