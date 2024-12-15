package com.furkandonertas.androidbitirmeproje

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // İlk fragment olarak HomeFragment'i yükle
        loadFragment(HomeFragment())

        // Navigation Bar: Ana Sayfa butonu
        val homeButton = findViewById<LinearLayout>(R.id.homeButtonContainer)
        homeButton.setOnClickListener {
            loadFragment(HomeFragment())
        }

        // Navigation Bar: Favoriler butonu
        val favoritesButton = findViewById<LinearLayout>(R.id.favoritesButtonContainer)
        favoritesButton.setOnClickListener {
            loadFragment(FavoritesFragment())
        }
    }

    /**
     * Fragment'ları yüklemek için kullanılan yardımcı fonksiyon
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment) // fragmentContainer FrameLayout'ını kullanıyoruz
            .addToBackStack(null)
            .commit()
    }
}
