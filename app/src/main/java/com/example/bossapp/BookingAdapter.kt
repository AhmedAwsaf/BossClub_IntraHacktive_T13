package com.example.bossapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingAdapter(private val bookings: List<Booking>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomTextView: TextView = view.findViewById(R.id.roomTextView)
        val timeTextView: TextView = view.findViewById(R.id.timeTextView)
        val purposeTextView: TextView = view.findViewById(R.id.purposeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.roomTextView.text = "Room: ${booking.roomNumber}"
        holder.timeTextView.text = "Time: ${booking.startTime} - ${booking.endTime}"
        holder.purposeTextView.text = "Purpose: ${booking.purpose}"
    }

    override fun getItemCount(): Int = bookings.size
}
