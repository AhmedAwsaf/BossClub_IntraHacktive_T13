package com.example.bossapp

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

        // Get references to the form elements
        val roomNumberEditText: EditText = findViewById(R.id.roomNumber)
        val startTimePicker: TimePicker = findViewById(R.id.startTime)
        val endTimePicker: TimePicker = findViewById(R.id.endTime)
        val purposeEditText: EditText = findViewById(R.id.purpose)
        val bookRoomButton: Button = findViewById(R.id.submitButton)

        // Set the TimePicker to 24-hour mode if desired
        startTimePicker.setIs24HourView(true)
        endTimePicker.setIs24HourView(true)

        // Set the OnClickListener for the button
        bookRoomButton.setOnClickListener {
            // Get the user input
            val roomNumber = roomNumberEditText.text.toString()
            val startHour = startTimePicker.hour
            val startMinute = startTimePicker.minute
            val endHour = endTimePicker.hour
            val endMinute = endTimePicker.minute
            val purpose = purposeEditText.text.toString()

            // Display a Toast message as confirmation (you can replace this with actual booking logic)
            Toast.makeText(this, "Room $roomNumber booked from $startHour:$startMinute to $endHour:$endMinute for: $purpose", Toast.LENGTH_LONG).show()
        }
    }
}
