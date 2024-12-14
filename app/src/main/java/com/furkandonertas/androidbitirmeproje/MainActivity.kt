package com.furkandonertas.androidbitirmeproje

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var startDate: String = ""
    private var endDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val groupSizeSpinner: Spinner = findViewById(R.id.groupSizeSpinner)
        val groupSizes = arrayOf("Grup boyutunu seçin...", "1-2", "3-10", "10+")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupSizes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        groupSizeSpinner.adapter = adapter

        val moodSpinner: Spinner = findViewById(R.id.moodSpinner)
        val moods = arrayOf("Ruh halinizi seçin...", "Enerjik", "Üzgün", "Macera İsteyen")
        val moodAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, moods)
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        moodSpinner.adapter = moodAdapter

        val familyStatusSpinner: Spinner = findViewById(R.id.familyStatusSpinner)
        val familyStatusOptions = arrayOf("Aile durumunu seçin...", "Aileyle", "Ailesiz")
        val familyStatusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, familyStatusOptions)
        familyStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        familyStatusSpinner.adapter = familyStatusAdapter

        val getLocationImage: ImageView = findViewById(R.id.getLocationImage)
        val locationInput: TextView = findViewById(R.id.locationInput)

        getLocationImage.setOnClickListener {
            checkLocationPermissionAndFetchLocation { location ->
                if (location.isNotEmpty()) {
                    locationInput.text = "Konum: $location"
                    Toast.makeText(this, "Konum alındı: $location", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Konum alınamadı! Lütfen GPS'in açık olduğundan emin olun.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val selectDateRangeButton: Button = findViewById(R.id.selectDateRangeButton)
        val selectedDateRangeTextView: TextView = findViewById(R.id.selectedDateRangeTextView)

        selectDateRangeButton.setOnClickListener {
            showDateRangePicker { start, end ->
                startDate = start
                endDate = end
                selectedDateRangeTextView.text = "Seçilen Tarih Aralığı: $startDate - $endDate"
            }
        }

        val getSuggestionsButton: Button = findViewById(R.id.getSuggestionsButton)
        getSuggestionsButton.setOnClickListener {
            val location = locationInput.text.toString().removePrefix("Konum: ").trim()
            val groupSize = groupSizeSpinner.selectedItem.toString()
            val mood = moodSpinner.selectedItem.toString()
            val familyStatus = familyStatusSpinner.selectedItem.toString()

            if (location.isEmpty() || location == "Konum alınamadı!") {
                Toast.makeText(this, "Lütfen geçerli bir konum girin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (groupSize == "Grup boyutunu seçin...") {
                Toast.makeText(this, "Lütfen bir grup boyutu seçin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (mood == "Ruh halinizi seçin...") {
                Toast.makeText(this, "Lütfen bir ruh hali seçin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (familyStatus == "Aile durumunu seçin...") {
                Toast.makeText(this, "Lütfen aile durumunuzu seçin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Lütfen bir tarih aralığı seçin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, EventSuggestionsActivity::class.java)
            intent.putExtra("LOCATION", location)
            intent.putExtra("GROUP_SIZE", groupSize)
            intent.putExtra("MOOD", mood)
            intent.putExtra("FAMILY_STATUS", familyStatus)
            intent.putExtra("START_DATE", startDate)
            intent.putExtra("END_DATE", endDate)
            startActivity(intent)
        }
    }

    private fun checkLocationPermissionAndFetchLocation(callback: (String) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            callback("")
            return
        }

        locationRequest = LocationRequest.create().apply {
            interval = 1000 // 1 saniyede bir konum güncellemesi
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val currentLocation = "${location.latitude},${location.longitude}"
                    Log.d("Konum", "Alınan Konum: $currentLocation")
                    callback(currentLocation)
                    fusedLocationClient.removeLocationUpdates(this) // Güncelleme durduruluyor
                } else {
                    callback("")
                }
            }
        }, null)
    }

    private fun showDateRangePicker(callback: (String, String) -> Unit) {
        val constraintsBuilder = CalendarConstraints.Builder()
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Tarih Aralığı Seç")
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startMillis = selection.first ?: 0
            val endMillis = selection.second ?: 0
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val startDate = formatter.format(Date(startMillis))
            val endDate = formatter.format(Date(endMillis))

            callback(startDate, endDate)
        }

        dateRangePicker.show(supportFragmentManager, "date_range_picker")
    }

    override fun onStop() {
        super.onStop()
        fusedLocationClient.flushLocations()
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.flushLocations()
        Log.d("AppLifecycle", "MainActivity kapandı")
    }
}
