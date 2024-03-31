package com.group22.cityspots.model

data class User(
    val userId: String = "",
    val username: String? = "",
    val profilePictureUrl: String? = "",
    val email: String? = ""
)