package com.example.energyview.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.energyview.models.Threshold
import com.example.energyview.models.User
import com.example.energyview.models.UserCreateDto
import com.example.energyview.network.RetrofitClient
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var thresholds by mutableStateOf<List<Threshold>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadAdminData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                users = RetrofitClient.apiService.getUsers()
                thresholds = RetrofitClient.apiService.getThresholds()
            } catch (e: Exception) {
                errorMessage = "Не вдалося завантажити дані адміна: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    // --- УПРАВЛІННЯ КОРИСТУВАЧАМИ ---
    fun addUser(username: String, fullName: String, role: String, passwordHash: String) {
        viewModelScope.launch {
            try {
                val dto = UserCreateDto(username, passwordHash, fullName, role)
                RetrofitClient.apiService.createUser(dto)
                users = RetrofitClient.apiService.getUsers()
            } catch (e: Exception) {
                errorMessage = "Помилка створення користувача: ${e.localizedMessage}"
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteUser(id)
                users = RetrofitClient.apiService.getUsers()
            } catch (e: Exception) {
                errorMessage = "Помилка видалення користувача: ${e.localizedMessage}"
            }
        }
    }

    // --- УПРАВЛІННЯ МЕЖАМИ (THRESHOLDS) ---
    fun addThreshold(sensorId: Int, minValue: Double, maxValue: Double, alertMessage: String?) {
        viewModelScope.launch {
            try {
                val newThreshold = Threshold(
                    thresholdId = 0,
                    sensorId = sensorId,
                    minValue = minValue,
                    maxValue = maxValue,
                    alertMessage = alertMessage
                )
                RetrofitClient.apiService.createThreshold(newThreshold)
                thresholds = RetrofitClient.apiService.getThresholds()
            } catch (e: Exception) {
                errorMessage = "Помилка додавання межі: ${e.localizedMessage}"
            }
        }
    }

    fun deleteThreshold(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteThreshold(id)
                thresholds = RetrofitClient.apiService.getThresholds()
            } catch (e: Exception) {
                errorMessage = "Помилка видалення межі: ${e.localizedMessage}"
            }
        }
    }
}