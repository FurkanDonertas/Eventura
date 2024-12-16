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

class EventAdapter(
    private val events: List<Event>,
    private val isFavoritesFragment: Boolean, // Favoriler ekranında mı olduğumuzu belirlemek için
    private val onFavoriteAction: (Event, Boolean) -> Unit // Favori ekleme/çıkarma için callback
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

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

        // Favorilere Ekle veya Favorilerden Çıkar butonu
        holder.favoriteButton.text = if (isFavoritesFragment) "Favorilerden Çıkar" else "Favorilere Ekle"
        holder.favoriteButton.setOnClickListener {
            val context = holder.itemView.context
            onFavoriteAction(event, isFavoritesFragment) // Favori işlemi için callback çağır

            // Favorilerden çıkarma durumunda toast mesajı göster
            if (isFavoritesFragment) {
                Toast.makeText(context, "${event.name} favorilerden çıkarıldı.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "${event.name} favorilere eklendi.", Toast.LENGTH_SHORT).show()
            }
        }

        // Satın alma işlemi
        holder.buyButton.setOnClickListener {
            val context = holder.itemView.context
            Toast.makeText(context, "Yönlendiriliyorsunuz...", Toast.LENGTH_SHORT).show() // Toast mesajı
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = events.size
}
