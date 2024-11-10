package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.bossapp.R
import com.google.firebase.auth.FirebaseAuth

class BookedRoomActivity : AppCompatActivity() {

    private val db: FirebaseFirestore = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booked_room)

        auth = FirebaseAuth.getInstance()

        val newbook = findViewById<TextView>(R.id.bookLink)

        newbook.setOnClickListener(
            {
                val intent = Intent(this, RoomActivity::class.java)
                startActivity(intent)
            }
        )
        showBookButton()
        // Load bookings from Firestore
        loadBookings()
    }

    private fun showBookButton(){
        val currentUser = auth.currentUser
        val userId = currentUser?.uid ?: ""
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val clr_level = document.getLong("club_clr_level")?.toInt() ?: 1
                    if(clr_level <= 1) {
                        findViewById<TextView>(R.id.bookLink).visibility = View.GONE
                    } else {
                        findViewById<TextView>(R.id.bookLink).visibility = View.VISIBLE
                    }
                }
                else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadBookings() {
        val bookingsLayout = findViewById<LinearLayout>(R.id.eventsLayout)
        bookingsLayout.removeAllViews()

        // Fetch all documents from "roomlist" collection
        db.collection("roomlist").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val roomId = document.id
                val bookingsMap = document.get("bookings") as? Map<String, Map<String, String>> ?: continue

                for (booking in bookingsMap.values) {
                    val clubName = booking["club_name"] ?: "No Club"
                    val date = booking["date"] ?: "N/A"
                    val startTime = booking["start_time"] ?: "N/A"
                    val endTime = booking["end_time"] ?: "N/A"
                    val purpose = booking["purpose"] ?: "N/A"

                    // Inflate and populate each booking item
                    val bookingView = LayoutInflater.from(this).inflate(R.layout.item_booking, bookingsLayout, false)

                    val roomIdText = bookingView.findViewById<TextView>(R.id.roomIdText)
                    val clubNameText = bookingView.findViewById<TextView>(R.id.clubNameText)
                    val dateText = bookingView.findViewById<TextView>(R.id.dateText)
                    val timeText = bookingView.findViewById<TextView>(R.id.timeText)
                    val purposeText = bookingView.findViewById<TextView>(R.id.purposeText)

                    // Set the data to the views
                    roomIdText.text = "Room ID: $roomId"
                    clubNameText.text = "Club: $clubName"
                    dateText.text = "Date: $date"
                    timeText.text = "Time: $startTime - $endTime"
                    purposeText.text = "Purpose: $purpose"

                    // Add the inflated booking view to the parent layout
                    bookingsLayout.addView(bookingView)
                }
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error fetching bookings: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
