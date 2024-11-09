package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class BookedRoomActivity : AppCompatActivity() {

    private lateinit var bookingsRecyclerView: RecyclerView
    private val bookings = mutableListOf<Booking>()
    private lateinit var bookingAdapter: BookingAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booked_room)

        bookingsRecyclerView = findViewById(R.id.bookingsRecyclerView)
        bookingAdapter = BookingAdapter(bookings)
        bookingsRecyclerView.layoutManager = LinearLayoutManager(this)
        bookingsRecyclerView.adapter = bookingAdapter

        val booklink = findViewById<TextView>(R.id.bookLink)
        booklink.setOnClickListener {
            val intent = Intent(this, RoomActivity::class.java)
            startActivity(intent)
        }

        loadBookings()
    }

    private fun loadBookings() {
        db.collection("roomlist").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val bookingsMap = document.get("bookings") as? Map<String, Map<String, String>> ?: continue
                    for (booking in bookingsMap.values) {
                        val bookingItem = Booking(
                            booking["club_name"] ?: "",
                            booking["date"] ?: "",
                            booking["start_time"] ?: "",
                            booking["end_time"] ?: "",
                            booking["purpose"] ?: ""
                        )
                        bookings.add(bookingItem)
                    }
                }
                bookingAdapter.notifyDataSetChanged()
            }
    }
}
