package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class RoomActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val roomNumberEditText: EditText = findViewById(R.id.roomNumber)
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val startTimePicker: TimePicker = findViewById(R.id.startTime)
        val endTimePicker: TimePicker = findViewById(R.id.endTime)
        val purposeEditText: EditText = findViewById(R.id.purpose)
        val bookRoomButton: Button = findViewById(R.id.submitButton)

        startTimePicker.setIs24HourView(false)
        endTimePicker.setIs24HourView(false)

        // Get the selected date from CalendarView
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = dateFormat.format(calendar.time)
        }

        bookRoomButton.setOnClickListener {
            val roomNumber = roomNumberEditText.text.toString()
            val startTime = formatTime(startTimePicker.hour, startTimePicker.minute)
            val endTime = formatTime(endTimePicker.hour, endTimePicker.minute)
            val purpose = purposeEditText.text.toString()

            if (roomNumber.isNotEmpty() && purpose.isNotEmpty() && selectedDate.isNotEmpty()) {
                saveRoomDataToFirestore(roomNumber, purpose, startTime, endTime, selectedDate)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // AM/PM format
        return timeFormat.format(calendar.time)
    }

    private fun saveRoomDataToFirestore(
        roomNumber: String,
        purpose: String,
        startTime: String,
        endTime: String,
        date: String
    ) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val club = document.getString("club") ?: "No Club"
                    val bookingData = hashMapOf(
                        "room_no" to roomNumber,
                        "purpose" to purpose,
                        "club_name" to club,
                        "start_time" to startTime,
                        "end_time" to endTime,
                        "date" to date
                    )

                    db.collection("rooms").add(bookingData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Room booked successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, BookedRoomActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error booking room: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
