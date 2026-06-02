package com.example.energyview.models

import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("deviceId") val deviceId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("location") val location: String? = null,
    @SerializedName("installedAt") val installedAt: String? = null,
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("maxPowerOutput") val maxPowerOutput: Double? = null,
    @SerializedName("sensors") val sensors: List<Sensor>? = emptyList()
)

data class Sensor(
    @SerializedName("sensorId") val sensorId: Int,
    @SerializedName("deviceId") val deviceId: Int,
    @SerializedName("sensorType") val sensorType: String?,
    @SerializedName("unit") val unit: String?,
    @SerializedName("description") val description: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("readings") val readings: List<SensorReadingItem>? = emptyList(),
    @SerializedName("thresholds") val thresholds: List<Threshold>? = emptyList()
)

data class SensorReadingItem(
    @SerializedName("readingId") val readingId: Long,
    @SerializedName("sensorId") val sensorId: Int,
    @SerializedName("value") val value: Double,
    @SerializedName("timestamp") val timestamp: String
)

// Оголошується ОДИН раз тут, щоб не було Redeclaration!
data class Threshold(
    @SerializedName("thresholdId") val thresholdId: Int,
    @SerializedName("sensorId") val sensorId: Int,
    @SerializedName("minValue") val minValue: Double,
    @SerializedName("maxValue") val maxValue: Double,
    @SerializedName("alertMessage") val alertMessage: String?
)

object AvailableSensors {
    val types = listOf(
        "Енергоспоживання (Power)" to "kWh",
        "Напруга (Voltage)" to "V",
        "Сила струму (Current)" to "A",
        "Температура (Temperature)" to "°C"
    )
}