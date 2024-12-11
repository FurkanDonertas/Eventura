package com.furkandonertas.androidbitirmeproje

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furkandonertas.androidbitirmeproje.adapters.Event
import com.furkandonertas.androidbitirmeproje.adapters.EventAdapter
import com.furkandonertas.androidbitirmeproje.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventSuggestionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_suggestions)

        val recyclerView: RecyclerView = findViewById(R.id.eventRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Intent ile gelen konum bilgisini alın
        val location = intent.getStringExtra("LOCATION") ?: "0,0"

        // API çağrısı yap ve RecyclerView'u güncelle
        RetrofitInstance.api.getEvents(latlong = location).enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    val events = response.body() ?: emptyList()

                    if (events.isNotEmpty()) {
                        // RecyclerView'u güncelle
                        recyclerView.adapter = EventAdapter(events)
                    } else {
                        // Eğer liste boşsa
                        Toast.makeText(this@EventSuggestionsActivity, "Etkinlik bulunamadı.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API Error", "Hata Kodu: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@EventSuggestionsActivity, "Etkinlikleri getirirken bir hata oluştu.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                // API çağrısı başarısız olduğunda
                Log.e("API Failure", "Bağlantı hatası: ${t.message}")
                Toast.makeText(this@EventSuggestionsActivity, "Bağlantı hatası: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
