package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BookedRoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booked_room)

        // Get booking details passed from RoomActivity
        val roomNumber = intent.getStringExtra("roomNumber") ?: "N/A"
        val startTime = intent.getStringExtra("startTime") ?: "N/A"
        val endTime = intent.getStringExtra("endTime") ?: "N/A"
        val purpose = intent.getStringExtra("purpose") ?: "N/A"

        // Display the booking details
        findViewById<TextView>(R.id.roomDetails).text = """
            Room Number: $roomNumber
            Start Time: $startTime
            End Time: $endTime
            Purpose: $purpose
        """.trimIndent()

        // Link to RoomActivity to book a new room
        findViewById<TextView>(R.id.bookLink).setOnClickListener {
            startActivity(Intent(this, RoomActivity::class.java))
        }
    }
}
