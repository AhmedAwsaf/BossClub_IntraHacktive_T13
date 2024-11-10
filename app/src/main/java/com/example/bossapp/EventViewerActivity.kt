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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class EventViewerActivity : AppCompatActivity() {

    private lateinit var eventsLayout: LinearLayout
    private val db = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_viewer)

        eventsLayout = findViewById(R.id.eventsLayout)

        auth = FirebaseAuth.getInstance()

        showEventButton()


        loadEvents()


        findViewById<TextView>(R.id.createEventLink).setOnClickListener {
            startActivity(Intent(this, CreateEventActivity::class.java))
        }
    }

    private fun showEventButton(){
        val currentUser = auth.currentUser
        val userId = currentUser?.uid ?: ""
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val clr_level = document.getLong("club_clr_level")?.toInt() ?: 1
                    if(clr_level <= 1) {
                        findViewById<TextView>(R.id.createEventLink).visibility = View.GONE
                    } else {
                        findViewById<TextView>(R.id.createEventLink).visibility = View.VISIBLE
                    }
                }
                else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
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


                eventsLayout.removeAllViews()

                val upcomingEvents = mutableListOf<View>()
                val pastEvents = mutableListOf<View>()
                val currentDate = Date()


                for (document in documents) {
                    val eventName = document.getString("eventName") ?: ""
                    val eventDescription = document.getString("eventDescription") ?: ""
                    val eventStartDate = document.getString("eventStartDate") ?: ""
                    val eventEndDate = document.getString("eventEndDate") ?: ""
                    val eventFeatures = document.get("eventFeatures") as? List<String> ?: emptyList()
                    val status = document.getString("status") ?: ""


                    val eventView = createEventView(eventName, eventDescription, eventStartDate, eventEndDate, eventFeatures, status)


                    val endDate = dateFormat.parse(eventEndDate)
                    if (endDate != null && endDate.before(currentDate)) {

                        pastEvents.add(eventView)
                    } else {

                        upcomingEvents.add(eventView)
                    }
                }


                for (eventView in upcomingEvents) {
                    eventsLayout.addView(eventView)
                    addSpacer()
                }

                val event_spacer = layoutInflater.inflate(R.layout.event_spacer, null)
                eventsLayout.addView(event_spacer)


                for (eventView in pastEvents) {
                    eventsLayout.addView(eventView)
                    addSpacer()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading events: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


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
