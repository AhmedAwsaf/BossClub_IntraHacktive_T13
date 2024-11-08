package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class RoomActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room)

        val roomNumberEditText: EditText = findViewById(R.id.roomNumber)
        val startTimePicker: TimePicker = findViewById(R.id.startTime)
        val endTimePicker: TimePicker = findViewById(R.id.endTime)
        val purposeEditText: EditText = findViewById(R.id.purpose)
        val bookRoomButton: Button = findViewById(R.id.submitButton)

        startTimePicker.setIs24HourView(true)
        endTimePicker.setIs24HourView(true)

        bookRoomButton.setOnClickListener {
            val roomNumber = roomNumberEditText.text.toString()
            val purpose = purposeEditText.text.toString()

            if (roomNumber.isNotEmpty() && purpose.isNotEmpty()) {
                val intent = Intent(this, BookedRoomActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
