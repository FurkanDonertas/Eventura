package com.furkandonertas.androidbitirmeproje.adapters

data class Event(
    val name: String,
    val url: String?,
    val startDate: String?,
    val startTime: String?, // Saat bilgisi eklendi
    val locationName: String?,
    val locationCity: String?,
    val imageUrl: String?
)

