package com.furkandonertas.androidbitirmeproje.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.furkandonertas.androidbitirmeproje.R

class EventAdapter(private val events: List<Event>) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {
    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.eventName)
        val date: TextView = view.findViewById(R.id.eventDate)
        val location: TextView = view.findViewById(R.id.eventLocation)
        val image: ImageView = view.findViewById(R.id.eventImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.name.text = event.name
        holder.date.text = event.startDate
        holder.location.text = "${event.locationName}, ${event.locationCity}"

        Glide.with(holder.itemView.context)
            .load(event.imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.url))
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = events.size
}

