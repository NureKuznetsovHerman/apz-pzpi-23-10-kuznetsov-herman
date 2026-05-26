package com.example.energyview.models

sealed interface DeviceUiState {
    object Loading : DeviceUiState
    data class Success(val devices: List<Device>) : DeviceUiState
    data class Error(val message: String) : DeviceUiState
}