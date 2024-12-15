package com.furkandonertas.androidbitirmeproje.adapters


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.furkandonertas.androidbitirmeproje.R
import com.furkandonertas.androidbitirmeproje.db.AppDatabase
import com.furkandonertas.androidbitirmeproje.db.FavoriteEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventAdapter(private val events: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.eventName)
        val date: TextView = view.findViewById(R.id.eventDate)
        val location: TextView = view.findViewById(R.id.eventLocation)
        val image: ImageView = view.findViewById(R.id.eventImage)
        val favoriteButton: Button = view.findViewById(R.id.favoriteButton)
        val buyButton: Button = view.findViewById(R.id.buyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]

        // Etkinlik bilgilerini ayarla
        holder.name.text = event.name
        holder.date.text = "${event.startDate} ${event.startTime ?: ""}" // Tarih ve saat bilgisi
        holder.location.text = "${event.locationName}, ${event.locationCity}"

        // Glide ile görsel yükleme
        Glide.with(holder.itemView.context)
            .load(event.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.image)

        // Favorilere ekleme işlemi
        holder.favoriteButton.setOnClickListener {
            val context = holder.itemView.context
            saveToFavorites(event, context)
        }

        // Satın alma işlemi
        holder.buyButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun saveToFavorites(event: Event, context: Context) {
        val favoriteEvent = FavoriteEvent(
            name = event.name,
            date = event.startDate,
            time = event.startTime,
            location = "${event.locationName}, ${event.locationCity}",
            imageUrl = event.imageUrl,
            url = event.url
        )

        val database = AppDatabase.getDatabase(context)
        val dao = database.favoriteEventDao()

        // Coroutine ile veritabanına kaydet
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(favoriteEvent)
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "${event.name} favorilere eklendi!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = events.size
}
