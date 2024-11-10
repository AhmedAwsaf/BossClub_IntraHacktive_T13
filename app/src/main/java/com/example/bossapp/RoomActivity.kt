package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class RoomActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var selectedDate: String = ""
    private lateinit var roomSpinner: Spinner
    private lateinit var roomId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.room)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val datePicker = findViewById<CalendarView>(R.id.calendarView)
        val startTimePicker: TimePicker = findViewById(R.id.startTime)
        val endTimePicker: TimePicker = findViewById(R.id.endTime)
        val purposeEditText: EditText = findViewById(R.id.purpose)
        val bookRoomButton: Button = findViewById(R.id.submitButton)
        roomSpinner = findViewById(R.id.roomSpinner)

        // Set up time pickers
        startTimePicker.setIs24HourView(false)
        endTimePicker.setIs24HourView(false)

        // Populate room spinner with room IDs from Firestore
        populateRoomSpinner()

        // Handle date selection from CalendarView
        datePicker.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            selectedDate = dateFormat.format(calendar.time)
        }

        // Handle room booking on button click
        bookRoomButton.setOnClickListener {
            val startTime = formatTime(startTimePicker.hour, startTimePicker.minute)
            val endTime = formatTime(endTimePicker.hour, endTimePicker.minute)
            val purpose = purposeEditText.text.toString()

            if (purpose.isNotEmpty() && selectedDate.isNotEmpty() && ::roomId.isInitialized) {
                if (isFutureDateTime(selectedDate, startTime) && isEndTimeAfterStartTime(startTime, endTime)) {
                    checkAndBookRoom(roomId, purpose, startTime, endTime, selectedDate)
                } else {
                    Toast.makeText(this, "Booking date/time must be in the future and end time must be after start time", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Populate the room spinner with room IDs from Firestore
    private fun populateRoomSpinner() {
        db.collection("roomlist").get().addOnSuccessListener { documents ->
            val roomIds = mutableListOf<String>()
            for (document in documents) {
                roomIds.add(document.id) // Use document ID as room ID
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, roomIds)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            roomSpinner.adapter = adapter

            roomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    roomId = roomIds[position]
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    // Check if selected date and time are in the future
    private fun isFutureDateTime(date: String, time: String): Boolean {
        val currentDateTime = Calendar.getInstance().time
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        val selectedDateTime = dateTimeFormat.parse("$date $time")
        return selectedDateTime?.after(currentDateTime) == true
    }

    // Check if the end time is after the start time
    private fun isEndTimeAfterStartTime(startTime: String, endTime: String): Boolean {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val start = timeFormat.parse(startTime)
        val end = timeFormat.parse(endTime)
        return end?.after(start) == true
    }

    // Format time to AM/PM format
    private fun formatTime(hour: Int, minute: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(calendar.time)
    }

    // Check for overlapping bookings and save the new booking if available
    private fun checkAndBookRoom(
        roomId: String,
        purpose: String,
        startTime: String,
        endTime: String,
        date: String
    ) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("roomlist").document(roomId).get()
                .addOnSuccessListener { roomDoc ->
                    val bookings = roomDoc.get("bookings") as? Map<String, Map<String, String>> ?: emptyMap()
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val requestedStart = timeFormat.parse(startTime) ?: return@addOnSuccessListener
                    val requestedEnd = timeFormat.parse(endTime) ?: return@addOnSuccessListener

                    var isOverlapping = false
                    for (booking in bookings.values) {
                        val bookedDate = booking["date"] ?: continue
                        if (bookedDate != date) continue

                        val bookedStart = timeFormat.parse(booking["start_time"] ?: continue) ?: continue
                        val bookedEnd = timeFormat.parse(booking["end_time"] ?: continue) ?: continue

                        if ((requestedStart.before(bookedEnd) && requestedEnd.after(bookedStart)) ||
                            (requestedStart == bookedStart || requestedEnd == bookedEnd)
                        ) {
                            isOverlapping = true
                            break
                        }
                    }

                    if (isOverlapping) {
                        Toast.makeText(this, "Time slot unavailable. Please choose another time.", Toast.LENGTH_LONG).show()
                    } else {
                        saveRoomBookingToFirestore(roomId, purpose, startTime, endTime, date)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching room data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Save booking details to Firestore with room document ID
    private fun saveRoomBookingToFirestore(
        roomId: String,
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
                    val bookingData = mapOf(
                        "document_id" to roomId,
                        "club_name" to club,
                        "date" to date,
                        "start_time" to startTime,
                        "end_time" to endTime,
                        "purpose" to purpose,
                        "username" to (currentUser.displayName ?: "Anonymous")
                    )

                    val roomRef = db.collection("roomlist").document(roomId)
                    roomRef.update("bookings.${System.currentTimeMillis()}", bookingData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Room booked successfully!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, BookedRoomActivity::class.java))
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
