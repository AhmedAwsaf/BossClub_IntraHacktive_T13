package com.example.bossapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RoomManager : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var roomListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_manager)

        db = FirebaseFirestore.getInstance()
        roomListContainer = findViewById(R.id.roomlist)

        val roomNumberInput = findViewById<EditText>(R.id.roomNumberInput)
        val createButton = findViewById<Button>(R.id.createButton)

        createButton.setOnClickListener {
            val roomNumber = roomNumberInput.text.toString().trim()
            if (roomNumber.isNotEmpty()) {
                createRoomDocument(roomNumber)
            }
        }

        loadRooms()
    }

    private fun createRoomDocument(roomNumber: String) {
        val roomData = hashMapOf(
            "available" to true,
            "bookings" to mapOf<String, Any>()
        )

        db.collection("roomlist").document(roomNumber)
            .set(roomData)
            .addOnSuccessListener {
                Toast.makeText(this, "Room $roomNumber created", Toast.LENGTH_SHORT).show()
                loadRooms() // Refresh room list
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error creating room", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadRooms() {
        roomListContainer.removeAllViews() // Clear existing views

        db.collection("roomlist")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val roomNumber = document.id
                    val available = document.getBoolean("available") ?: true
                    val bookings = document.get("bookings") as? Map<*, *> ?: emptyMap<String, Any>()
                    val bookingCount = bookings.size

                    val roomView = layoutInflater.inflate(R.layout.item_room_card, roomListContainer, false)

                    val roomNumberText = roomView.findViewById<TextView>(R.id.roomNumberText)
                    val availabilityToggle = roomView.findViewById<ToggleButton>(R.id.availabilityToggle)
                    val bookingCounterText = roomView.findViewById<TextView>(R.id.bookingCounterText)
                    val addBookingButton = roomView.findViewById<Button>(R.id.addBookingButton)
                    val deleteRoomButton = roomView.findViewById<Button>(R.id.deleteRoomButton)

                    roomNumberText.text = "Room $roomNumber"
                    availabilityToggle.isChecked = available
                    bookingCounterText.text = "Bookings: $bookingCount"

                    availabilityToggle.setOnCheckedChangeListener { _, isChecked ->
                        db.collection("roomlist").document(roomNumber)
                            .update("available", isChecked)
                    }

                    addBookingButton.setOnClickListener {
                        addBooking(roomNumber)
                    }

                    deleteRoomButton.setOnClickListener {
                        deleteRoom(roomNumber)
                    }

                    roomListContainer.addView(roomView)
                }
            }
    }

    private fun addBooking(roomNumber: String) {

    }

    private fun deleteRoom(roomNumber: String) {
        db.collection("roomlist").document(roomNumber)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Room $roomNumber deleted", Toast.LENGTH_SHORT).show()
                loadRooms() // Refresh room list
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error deleting room", Toast.LENGTH_SHORT).show()
            }
    }
}

