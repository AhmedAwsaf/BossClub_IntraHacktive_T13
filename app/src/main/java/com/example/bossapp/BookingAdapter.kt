package com.example.bossapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Booking(
    val clubName: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val purpose: String
)

class BookingAdapter(private val bookings: List<Booking>) :
    RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clubName: TextView = itemView.findViewById(R.id.clubNameText)
        val date: TextView = itemView.findViewById(R.id.dateText)
        val time: TextView = itemView.findViewById(R.id.timeText)
        val purpose: TextView = itemView.findViewById(R.id.purpose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

        // Use string resources with placeholders
        holder.clubName.text = holder.itemView.context.getString(R.string.club_name_placeholder, booking.clubName)
        holder.date.text = holder.itemView.context.getString(R.string.date_placeholder, booking.date)
        holder.time.text = holder.itemView.context.getString(R.string.time_placeholder, booking.startTime, booking.endTime)
        holder.purpose.text = holder.itemView.context.getString(R.string.purpose_placeholder, booking.purpose)
    }

    override fun getItemCount(): Int = bookings.size
}
