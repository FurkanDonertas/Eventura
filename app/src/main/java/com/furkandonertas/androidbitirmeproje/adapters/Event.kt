package com.furkandonertas.androidbitirmeproje.adapters

data class Event(
    val name: String?, // Etkinlik adı
    val url: String?, // Etkinlik URL'si
    val startDate: String?, // Başlangıç tarihi
    val locationName: String?, // Mekan adı
    val locationCity: String?, // Şehir adı
    val imageUrl: String? // Resim URL'si
)
