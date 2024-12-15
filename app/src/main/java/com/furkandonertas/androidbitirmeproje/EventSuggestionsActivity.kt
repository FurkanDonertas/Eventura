package com.furkandonertas.androidbitirmeproje

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furkandonertas.androidbitirmeproje.adapters.Event
import com.furkandonertas.androidbitirmeproje.adapters.EventAdapter
import com.furkandonertas.androidbitirmeproje.api.RetrofitInstance
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EventSuggestionsActivity : AppCompatActivity() {

    companion object {
        private const val API_KEY = "Cuhqqv1J6lbBGp4oDuigIMegAd8qpBul" // Ticketmaster API Key
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_suggestions)

        val recyclerView: RecyclerView = findViewById(R.id.eventRecyclerView)
        val progressBar: ProgressBar = findViewById(R.id.loadingProgressBar)
        val emptyMessageTextView: TextView = findViewById(R.id.emptyMessageTextView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Kullanıcı girdileri
        val mood = intent.getStringExtra("MOOD") ?: "Üzgün"
        val groupSize = intent.getStringExtra("GROUP_SIZE") ?: "1-2"
        val familyStatus = intent.getStringExtra("FAMILY_STATUS") ?: "Ailesiz"

        // Öneri türünü belirle
        val classification = determineEventClassification(mood, groupSize, familyStatus)

        // Tarih ve konum bilgileri
        val location = intent.getStringExtra("LOCATION") ?: "37.7749,-122.4194"
        val startDate = intent.getStringExtra("START_DATE") ?: ""
        val endDate = intent.getStringExtra("END_DATE") ?: ""

        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(this, "Tarih aralığı eksik!", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("API Request", "Konum: $location, Başlangıç: $startDate, Bitiş: $endDate, Tür: $classification")

        progressBar.visibility = View.VISIBLE

        // API çağrısı
        RetrofitInstance.api.getEvents(
            apiKey = API_KEY,
            latlong = location,
            classification = classification,
            startDateTime = "${startDate}T00:00:00Z",
            endDateTime = "${endDate}T23:59:59Z"
        ).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val jsonResponse = response.body()
                    val events = jsonResponse?.let { parseEventsFromJson(it) } ?: emptyList()

                    if (events.isEmpty()) {
                        emptyMessageTextView.text = "Bu kriterlere uygun etkinlik bulunamadı."
                        emptyMessageTextView.visibility = View.VISIBLE
                    } else {
                        recyclerView.adapter = EventAdapter(events)
                        emptyMessageTextView.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(this@EventSuggestionsActivity, "Etkinlikleri getirirken hata oluştu.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@EventSuggestionsActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })



    }

    private fun parseEventsFromJson(response: JsonObject): List<Event> {
        val events = mutableListOf<Event>()
        val embedded = response.getAsJsonObject("_embedded")?.getAsJsonArray("events")
        embedded?.forEach { eventElement ->
            val eventObj = eventElement.asJsonObject
            val name = eventObj.get("name").asString
            val url = eventObj.get("url")?.asString
            val startDateObj = eventObj.getAsJsonObject("dates")?.getAsJsonObject("start")
            val startDate = startDateObj?.get("localDate")?.asString
            val startTime = startDateObj?.get("localTime")?.asString // Saat bilgisi burada alınıyor
            val venue = eventObj.getAsJsonObject("_embedded")?.getAsJsonArray("venues")?.get(0)?.asJsonObject
            val locationName = venue?.get("name")?.asString
            val locationCity = venue?.getAsJsonObject("city")?.get("name")?.asString
            val imageUrl = eventObj.getAsJsonArray("images")?.get(0)?.asJsonObject?.get("url")?.asString

            events.add(Event(name, url, startDate, startTime, locationName, locationCity, imageUrl))
        }
        return events
    }

    private fun determineEventClassification(mood: String, groupSize: String, familyStatus: String): String {
        return when {
            mood == "Üzgün" && groupSize == "1-2" && familyStatus == "Ailesiz" -> "comedy"
            mood == "Enerjik" && groupSize == "3-10" && familyStatus == "Aileyle" -> "music"
            mood == "Macera İsteyen" && groupSize == "10+" && familyStatus == "Ailesiz" -> "sports"
            mood == "Enerjik" && groupSize == "1-2" && familyStatus == "Aileyle" -> "festival"
            mood == "Üzgün" && groupSize == "10+" && familyStatus == "Aileyle" -> "theater"
            mood == "Macera İsteyen" && groupSize == "1-2" && familyStatus == "Ailesiz" -> "outdoors"
            mood == "Üzgün" && groupSize == "3-10" && familyStatus == "Ailesiz" -> "drama"
            mood == "Enerjik" && groupSize == "10+" && familyStatus == "Ailesiz" -> "concert"
            mood == "Macera İsteyen" && groupSize == "3-10" && familyStatus == "Aileyle" -> "adventure"
            mood == "Üzgün" && groupSize == "1-2" && familyStatus == "Aileyle" -> "museum"
            mood == "Macera İsteyen" && groupSize == "10+" && familyStatus == "Aileyle" -> "travel"
            mood == "Enerjik" && groupSize == "10+" && familyStatus == "Aileyle" -> "parade"
            mood == "Üzgün" && groupSize == "3-10" && familyStatus == "Aileyle" -> "opera"
            mood == "Macera İsteyen" && groupSize == "1-2" && familyStatus == "Aileyle" -> "hiking"
            else -> "other"
        }
    }
}





