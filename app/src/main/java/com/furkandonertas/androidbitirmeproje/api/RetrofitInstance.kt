package com.furkandonertas.androidbitirmeproje.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://app.ticketmaster.com/discovery/v2/"

    val api: EventApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Gson JSON dönüştürücü
            .build()
            .create(EventApiService::class.java) // API arayüzü
    }
}
