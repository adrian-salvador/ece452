package com.group22.cityspots.model

sealed class UiState {
    class Success(val successMessage: String) : UiState()
    class Error(val errorMessage: String) : UiState()
}