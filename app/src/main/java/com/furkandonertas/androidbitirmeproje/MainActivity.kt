package com.furkandonertas.androidbitirmeproje

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: com.google.android.gms.location.LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Konum servislerini başlat
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Grup boyutu spinner'ı ayarla
        val groupSizeSpinner: Spinner = findViewById(R.id.groupSizeSpinner)
        val groupSizes = arrayOf("1-2", "3-5", "6-10", "10+")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupSizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupSizeSpinner.adapter = adapter

        // Ruh hali spinner'ı ayarla
        val moodSpinner: Spinner = findViewById(R.id.moodSpinner)
        val moods = arrayOf("Enerjik", "Rahat", "Macera")
        val moodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moods)
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        moodSpinner.adapter = moodAdapter

        // Konum görseli
        val getLocationImage: ImageView = findViewById(R.id.getLocationImage)
        val locationInput: TextView = findViewById(R.id.locationInput)

        // Görsele tıklandığında konum al
        getLocationImage.setOnClickListener {
            Toast.makeText(this, "Konum alınıyor...", Toast.LENGTH_SHORT).show()
            getDeviceLocation { location ->
                if (location.isNotEmpty()) {
                    locationInput.text = "Konum: $location"
                    Toast.makeText(this, "Konum: $location", Toast.LENGTH_SHORT).show()
                } else {
                    locationInput.text = "Konum alınamadı!"
                    Toast.makeText(this, "Konum alınamadı!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Önerileri Gör butonu
        val getSuggestionsButton: Button = findViewById(R.id.getSuggestionsButton)
        getSuggestionsButton.setOnClickListener {
            val location = locationInput.text.toString()
            val groupSize = groupSizeSpinner.selectedItem.toString()
            val mood = moodSpinner.selectedItem.toString() // Ruh hali seçimi

            if (location.isNotEmpty()) {
                val intent = Intent(this, EventSuggestionsActivity::class.java)
                intent.putExtra("LOCATION", location)
                intent.putExtra("GROUP_SIZE", groupSize)
                intent.putExtra("MOOD", mood) // Ruh halini gönder
                startActivity(intent)
            } else {
                Toast.makeText(this, "Lütfen bir konum alın!", Toast.LENGTH_SHORT).show()
            }
        }
    }




    private fun getDeviceLocation(callback: (String) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                callback("${location.latitude},${location.longitude}")
            } else {
                callback("")
            }
        }.addOnFailureListener { exception ->
            Log.e("LocationError", "Konum alınırken hata oluştu: ${exception.message}")
            callback("")
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}
