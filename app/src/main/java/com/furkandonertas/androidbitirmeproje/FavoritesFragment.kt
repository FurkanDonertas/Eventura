package com.furkandonertas.androidbitirmeproje

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furkandonertas.androidbitirmeproje.adapters.Event
import com.furkandonertas.androidbitirmeproje.adapters.EventAdapter
import com.furkandonertas.androidbitirmeproje.db.AppDatabase
import com.furkandonertas.androidbitirmeproje.db.FavoriteEvent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerView = view.findViewById(R.id.favoritesRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadFavorites()

        return view
    }

    private fun loadFavorites() {
        val database = AppDatabase.getDatabase(requireContext())
        val dao = database.favoriteEventDao()

        CoroutineScope(Dispatchers.IO).launch {
            dao.getAllFavorites().collect { favorites ->
                val events = favorites.map { favorite ->
                    Event(
                        name = favorite.name,
                        startDate = favorite.date,
                        startTime = favorite.time,
                        locationName = favorite.location?.split(",")?.getOrNull(0),
                        locationCity = favorite.location?.split(",")?.getOrNull(1),
                        imageUrl = favorite.imageUrl,
                        url = favorite.url
                    )
                }

                withContext(Dispatchers.Main) {
                    recyclerView.adapter = EventAdapter(events)
                }
            }
        }
    }

    private fun deleteFromFavorites(favorite: FavoriteEvent) {
        val database = AppDatabase.getDatabase(requireContext())
        val dao = database.favoriteEventDao()

        CoroutineScope(Dispatchers.IO).launch {
            dao.delete(favorite)
            withContext(Dispatchers.Main) {
                loadFavorites() // Favoriler yeniden yüklensin
            }
        }
    }
}