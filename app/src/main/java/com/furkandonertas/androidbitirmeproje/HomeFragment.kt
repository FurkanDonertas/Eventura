package com.furkandonertas.androidbitirmeproje

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var startDate: String = ""
    private var endDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bileşenleri bağlama
        val locationInput: TextView = view.findViewById(R.id.locationInput)
        val getLocationImage: ImageView = view.findViewById(R.id.getLocationImage)
        val groupSizeSpinner: Spinner = view.findViewById(R.id.groupSizeSpinner)
        val moodSpinner: Spinner = view.findViewById(R.id.moodSpinner)
        val familyStatusSpinner: Spinner = view.findViewById(R.id.familyStatusSpinner)
        val selectDateRangeButton: Button = view.findViewById(R.id.selectDateRangeButton)
        val selectedDateRangeTextView: TextView = view.findViewById(R.id.selectedDateRangeTextView)
        val getSuggestionsButton: Button = view.findViewById(R.id.getSuggestionsButton)

        // Konum alma işlevi
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        getLocationImage.setOnClickListener {
            checkLocationPermissionAndFetchLocation { location ->
                if (location.isNotEmpty()) {
                    locationInput.text = "Konum: $location"
                    Toast.makeText(requireContext(), "Konum alındı: $location", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Konum alınamadı! GPS'i kontrol edin.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Spinner'ları ayarla
        setupSpinners(groupSizeSpinner, moodSpinner, familyStatusSpinner)

        // Tarih aralığı seçme
        selectDateRangeButton.setOnClickListener {
            showDateRangePicker { start, end ->
                startDate = start
                endDate = end
                selectedDateRangeTextView.text = "Seçilen Tarih Aralığı: $startDate - $endDate"
            }
        }

        // Önerileri Gör butonu
        getSuggestionsButton.setOnClickListener {
            val location = locationInput.text.toString().removePrefix("Konum: ").trim()
            val groupSize = groupSizeSpinner.selectedItem.toString()
            val mood = moodSpinner.selectedItem.toString()
            val familyStatus = familyStatusSpinner.selectedItem.toString()

            if (validateInputs(location, groupSize, mood, familyStatus)) {
                Toast.makeText(requireContext(), "Öneriler yükleniyor...", Toast.LENGTH_SHORT).show()

                // Burada EventSuggestionsActivity'ye geçiş yapılabilir
                val intent = Intent(requireContext(), EventSuggestionsActivity::class.java)
                intent.putExtra("LOCATION", location)
                intent.putExtra("GROUP_SIZE", groupSize)
                intent.putExtra("MOOD", mood)
                intent.putExtra("FAMILY_STATUS", familyStatus)
                intent.putExtra("START_DATE", startDate)
                intent.putExtra("END_DATE", endDate)
                startActivity(intent)
            }
        }
    }

    private fun setupSpinners(
        groupSizeSpinner: Spinner,
        moodSpinner: Spinner,
        familyStatusSpinner: Spinner
    ) {
        val groupSizes = arrayOf("Grup boyutunu seçin...", "1-2", "3-10", "10+")
        groupSizeSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            groupSizes
        )

        val moods = arrayOf("Ruh halinizi seçin...", "Enerjik", "Üzgün", "Macera İsteyen")
        moodSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            moods
        )

        val familyStatusOptions = arrayOf("Aile durumunu seçin...", "Aileyle", "Ailesiz")
        familyStatusSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            familyStatusOptions
        )
    }

    private fun validateInputs(location: String, groupSize: String, mood: String, familyStatus: String): Boolean {
        if (location.isEmpty() || location == "Konum alınamadı!") {
            Toast.makeText(requireContext(), "Lütfen geçerli bir konum girin!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (groupSize == "Grup boyutunu seçin...") {
            Toast.makeText(requireContext(), "Lütfen bir grup boyutu seçin!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (mood == "Ruh halinizi seçin...") {
            Toast.makeText(requireContext(), "Lütfen bir ruh hali seçin!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (familyStatus == "Aile durumunu seçin...") {
            Toast.makeText(requireContext(), "Lütfen aile durumunuzu seçin!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (startDate.isEmpty() || endDate.isEmpty()) {
            Toast.makeText(requireContext(), "Lütfen bir tarih aralığı seçin!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkLocationPermissionAndFetchLocation(callback: (String) -> Unit) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1001)
            callback("")
            return
        }

        locationRequest = LocationRequest.create().apply {
            interval = 1000
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
                    fusedLocationClient.removeLocationUpdates(this)
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

        dateRangePicker.show(parentFragmentManager, "date_range_picker")
    }
}
