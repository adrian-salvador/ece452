package com.group22.cityspots.presentation.sign_in

data class SignInResult(
    val data: com.group22.cityspots.presentation.sign_in.UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?
)
