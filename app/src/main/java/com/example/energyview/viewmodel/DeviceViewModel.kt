package com.example.energyview.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.energyview.models.Device
import com.example.energyview.models.DeviceUiState
import com.example.energyview.models.SensorReadingDto
import com.example.energyview.network.RetrofitClient
import kotlinx.coroutines.launch

class DeviceViewModel : ViewModel() {
    var uiState: DeviceUiState by mutableStateOf(DeviceUiState.Loading)
        private set

    init {
        fetchDevices()
    }

    fun fetchDevices() {
        viewModelScope.launch {
            uiState = DeviceUiState.Loading
            try {
                val list = RetrofitClient.apiService.getDevices()
                uiState = DeviceUiState.Success(list)
            } catch (e: Exception) {
                uiState = DeviceUiState.Error("Не вдалося завантажити дані: ${e.localizedMessage}")
            }
        }
    }

    fun addDevice(device: Device) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.createDevice(device)
                fetchDevices() // Перезавантажуємо список після додавання
            } catch (e: Exception) {
                uiState = DeviceUiState.Error("Помилка додавання пристрою: ${e.localizedMessage}")
            }
        }
    }

    fun updateDevice(id: Int, device: Device) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.updateDevice(id, device)
                fetchDevices()
            } catch (e: Exception) {
                uiState = DeviceUiState.Error("Помилка оновлення пристрою: ${e.localizedMessage}")
            }
        }
    }

    fun deleteDevice(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteDevice(id)
                fetchDevices()
            } catch (e: Exception) {
                uiState = DeviceUiState.Error("Помилка видалення: ${e.localizedMessage}")
            }
        }
    }

    fun addSensorReading(sensorId: Int, value: Double) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.sendReading(SensorReadingDto(sensorId, value))
                fetchDevices() // Оновлюємо пристрої, щоб одразу побачити нові значення
            } catch (e: Exception) {
                uiState = DeviceUiState.Error("Помилка надсилання показника: ${e.localizedMessage}")
            }
        }
    }
}