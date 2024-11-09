package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EventViewerActivity : AppCompatActivity() {

    private lateinit var eventsLayout: LinearLayout
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_viewer)

        // Initialize the layout
        eventsLayout = findViewById(R.id.eventsLayout)

        // Fetch events from Firestore
        loadEvents()

        // Set up the Create Event link
        findViewById<TextView>(R.id.createEventLink).setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }
    }

    // Fetch events from Firestore and display them
    private fun loadEvents() {
        db.collection("events").get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val event = document.toObject(Event::class.java)
                        addEventView(event)
                    }
                } else {
                    Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading events: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Dynamically add each event to the LinearLayout
    private fun addEventView(event: Event) {
        val eventView = LayoutInflater.from(this).inflate(R.layout.item_event, null)

        val eventNameText = eventView.findViewById<TextView>(R.id.eventNameText)
        val eventDescriptionText = eventView.findViewById<TextView>(R.id.eventDescriptionText)
        val eventDateText = eventView.findViewById<TextView>(R.id.eventDateText)
        val eventStatusText = eventView.findViewById<TextView>(R.id.eventStatusText)

        eventNameText.text = event.eventName
        eventDescriptionText.text = event.eventDescription
        eventDateText.text = "From: ${event.eventStartDate} To: ${event.eventEndDate}"
        eventStatusText.text = "Status: ${event.status}"

        eventsLayout.addView(eventView)
    }
}
