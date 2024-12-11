package com.furkandonertas.androidbitirmeproje.api

import com.furkandonertas.androidbitirmeproje.adapters.Event
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EventApiService {
    @GET("events")
    fun getEvents(
        @Query("latlong") latlong: String,
        @Query("radius") radius: Int = 10
    ): Call<List<Event>>
}
