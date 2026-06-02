package com.example.energyview.models

import com.google.gson.annotations.SerializedName

data class SensorReadingDto(
    @SerializedName("sensorId") val sensorId: Int,
    @SerializedName("value") val value: Double
)