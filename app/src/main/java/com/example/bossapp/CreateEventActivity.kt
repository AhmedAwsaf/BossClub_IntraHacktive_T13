package com.example.bossapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateEventActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_event_activity)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val eventNameEditText = findViewById<EditText>(R.id.eventName)
        val eventDescriptionEditText = findViewById<EditText>(R.id.eventDescription)
        val eventStartDateEditText = findViewById<EditText>(R.id.eventStartDate)
        val eventEndDateEditText = findViewById<EditText>(R.id.eventEndDate)
        val eventFeaturesEditText = findViewById<EditText>(R.id.eventFeatures)
        val createEventButton = findViewById<Button>(R.id.createEventButton)

        createEventButton.setOnClickListener {
            val eventName = eventNameEditText.text.toString().trim()
            val eventDescription = eventDescriptionEditText.text.toString().trim()
            val eventStartDate = eventStartDateEditText.text.toString().trim()
            val eventEndDate = eventEndDateEditText.text.toString().trim()
            val eventFeatures = eventFeaturesEditText.text.toString().split(",").map { it.trim() }

            if (eventName.isNotEmpty() && eventDescription.isNotEmpty() && eventStartDate.isNotEmpty() && eventEndDate.isNotEmpty()) {
                // Fetch current user’s club and name
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    firestore.collection("users").document(currentUser.uid).get()
                        .addOnSuccessListener { document ->
                            val club = document.getString("club") ?: "No Club"
                            val userName = document.getString("username") ?: "Anonymous"

                            // Create the event with the user’s club and name
                            val event = Event(
                                eventName = eventName,
                                eventDescription = eventDescription,
                                eventStartDate = eventStartDate,
                                eventEndDate = eventEndDate,
                                eventFeatures = eventFeatures,
                                club = club,
                                addedBy = currentUser.uid
                            )

                            firestore.collection("events")
                                .add(event)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Failed to create event: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to fetch user data: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
