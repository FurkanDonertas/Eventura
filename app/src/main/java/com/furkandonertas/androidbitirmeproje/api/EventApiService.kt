package com.furkandonertas.androidbitirmeproje.api

import com.furkandonertas.androidbitirmeproje.adapters.Event
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EventApiService {
    @GET("events.json")
    fun getEvents(
        @Query("apikey") apiKey: String,
        @Query("latlong") latlong: String,
        @Query("radius") radius: Int = 50,
        @Query("classificationName") classification: String,
        @Query("startDateTime") startDateTime: String,
        @Query("endDateTime") endDateTime: String
    ): Call<JsonObject> // Dönüş tipi JsonObject oldu
}

