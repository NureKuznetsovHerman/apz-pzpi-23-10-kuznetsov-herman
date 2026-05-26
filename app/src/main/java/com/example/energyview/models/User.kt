package com.example.energyview.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("role") val role: String,
    @SerializedName("isActive") val isActive: Boolean
)

data class UserCreateDto(
    @SerializedName("username") val username: String,
    @SerializedName("passwordHash") val passwordHash: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("role") val role: String
)