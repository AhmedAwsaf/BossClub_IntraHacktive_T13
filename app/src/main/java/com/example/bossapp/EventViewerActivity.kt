package com.example.bossapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class EventViewerActivity : AppCompatActivity() {

    private lateinit var eventsLayout: LinearLayout
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_viewer)

        eventsLayout = findViewById(R.id.eventsLayout)

        // Load events sorted by eventStartDate in ascending order
        loadEvents()

        // Hyperlink to create a new event
        findViewById<TextView>(R.id.createEventLink).setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }
    }

    private fun loadEvents() {
        // Fetch events sorted by the start date in ascending order
        db.collection("events")
            .orderBy("eventStartDate", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Clear the existing layout
                eventsLayout.removeAllViews()

                val upcomingEvents = mutableListOf<View>()
                val pastEvents = mutableListOf<View>()
                val currentDate = Date() // Current date to compare with event end dates

                // Display each event
                for (document in documents) {
                    val eventName = document.getString("eventName") ?: ""
                    val eventDescription = document.getString("eventDescription") ?: ""
                    val eventStartDate = document.getString("eventStartDate") ?: ""
                    val eventEndDate = document.getString("eventEndDate") ?: ""
                    val eventFeatures = document.get("eventFeatures") as? List<String> ?: emptyList()
                    val status = document.getString("status") ?: ""

                    // Create a view for each event
                    val eventView = createEventView(eventName, eventDescription, eventStartDate, eventEndDate, eventFeatures, status)

                    // Determine if the event has passed or is upcoming
                    val endDate = dateFormat.parse(eventEndDate)
                    if (endDate != null && endDate.before(currentDate)) {
                        // If the event has already ended, add it to past events list
                        pastEvents.add(eventView)
                    } else {
                        // If the event is upcoming or ongoing, add it to upcoming events list
                        upcomingEvents.add(eventView)
                    }
                }

                // Add upcoming events to the layout first
                for (eventView in upcomingEvents) {
                    eventsLayout.addView(eventView)
                    addSpacer()
                }

                val event_spacer = layoutInflater.inflate(R.layout.event_spacer, null)
                eventsLayout.addView(event_spacer)

                // Add past events to the layout after upcoming events
                for (eventView in pastEvents) {
                    eventsLayout.addView(eventView)
                    addSpacer()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading events: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Helper function to add spacing between events
    private fun addSpacer() {
        val spacer = View(this)
        spacer.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            16
        )


        eventsLayout.addView(spacer)
    }

    @SuppressLint("MissingInflatedId")
    private fun createEventView(
        eventName: String,
        eventDescription: String,
        eventStartDate: String,
        eventEndDate: String,
        eventFeatures: List<String>,
        status : String
    ): View {
        val eventView = layoutInflater.inflate(R.layout.item_event, null)

        val eventNameTextView = eventView.findViewById<TextView>(R.id.eventName)
        val eventDescriptionTextView = eventView.findViewById<TextView>(R.id.eventDescription)
        val eventDatesTextView = eventView.findViewById<TextView>(R.id.eventDates)
        val eventFeaturesTextView = eventView.findViewById<TextView>(R.id.eventFeatures)
        val statusTextView = eventView.findViewById<TextView>(R.id.status)
        val statusImageView = eventView.findViewById<ImageView>(R.id.statusImageView)
        val statusApprovedImageView = eventView.findViewById<ImageView>(R.id.statusApprovedImageView)

        eventNameTextView.text = eventName
        eventDescriptionTextView.text = eventDescription
        eventDatesTextView.text = "From: $eventStartDate To: $eventEndDate"
        eventFeaturesTextView.text = "Features: ${eventFeatures.joinToString(", ")}"


        if (status == "approved"){
            statusImageView.visibility = View.GONE
            statusApprovedImageView.visibility = View.VISIBLE
            statusTextView.text = "Approved Event"
        } else if (status == "rejected"){
            statusImageView.visibility = View.GONE
            statusApprovedImageView.visibility = View.GONE
            statusTextView.text = "Rejected Event"
        }
        else{
            statusImageView.visibility = View.VISIBLE
            statusApprovedImageView.visibility = View.GONE
            statusTextView.text = "Waiting for Approval"
        }

        return eventView
    }
}
