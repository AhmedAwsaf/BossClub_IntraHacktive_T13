package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BookedRoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booked_room)


        // Link to RoomActivity to book a new room
        findViewById<TextView>(R.id.bookLink).setOnClickListener {
            startActivity(Intent(this, RoomActivity::class.java))
        }
    }
}
