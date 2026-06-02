package com.example.energyview.network

import com.example.energyview.models.*
import retrofit2.http.*

interface EnergyApiService {

    // Пристрої
    @GET("api/devices")
    suspend fun getDevices(): List<Device>

    @POST("api/devices")
    suspend fun createDevice(@Body device: Device): Device

    @PUT("api/devices/{id}")
    suspend fun updateDevice(@Path("id") id: Int, @Body device: Device): Device

    @DELETE("api/devices/{id}")
    suspend fun deleteDevice(@Path("id") id: Int): Any

    // Користувачі
    @GET("api/Users")
    suspend fun getUsers(): List<User>

    @POST("api/Users")
    suspend fun createUser(@Body dto: UserCreateDto): User

    @DELETE("api/Users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Any

    // Внесення нових показників датчиків
    @POST("api/readings")
    suspend fun sendReading(@Body dto: SensorReadingDto): Any

    // Межі (Thresholds)
    @GET("api/Thresholds")
    suspend fun getThresholds(): List<Threshold>

    @POST("api/Thresholds")
    suspend fun createThreshold(@Body threshold: Threshold): Threshold

    @DELETE("api/Thresholds/{id}")
    suspend fun deleteThreshold(@Path("id") id: Int): Any
}