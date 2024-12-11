package com.furkandonertas.androidbitirmeproje.adapters

data class Event(
    val title: String,
    val details: String,
    val date: String,
    val location: Location
)

data class Location(
    val latitude: Double,
    val longitude: Double
)
