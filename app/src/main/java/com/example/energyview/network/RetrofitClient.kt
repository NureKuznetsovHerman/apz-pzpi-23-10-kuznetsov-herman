package com.example.energyview.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // 10.0.2.2 - це магічна адреса, яка вказує емулятору Android на твій localhost (комп'ютер).
    // Якщо у Visual Studio твій бекенд запускається на іншому порту (не 5000), зміни його тут!
    private const val BASE_URL = "http://10.0.2.2:5114"

    val apiService: EnergyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(EnergyApiService::class.java)
    }
}