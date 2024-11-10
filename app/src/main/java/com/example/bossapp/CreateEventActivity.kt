package com.example.bossapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CreateEventActivity : AppCompatActivity() {

    private lateinit var eventNameEditText: EditText
    private lateinit var eventDescriptionEditText: EditText
    private lateinit var eventStartDateEditText: EditText
    private lateinit var eventEndDateEditText: EditText
    private lateinit var eventFeaturesEditText: EditText
    private lateinit var createEventButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_event_activity)


        eventNameEditText = findViewById(R.id.eventName)
        eventDescriptionEditText = findViewById(R.id.eventDescription)
        eventStartDateEditText = findViewById(R.id.eventStartDate)
        eventEndDateEditText = findViewById(R.id.eventEndDate)
        eventFeaturesEditText = findViewById(R.id.eventFeatures)
        createEventButton = findViewById(R.id.createEventButton)


        eventStartDateEditText.setOnClickListener { showDatePicker(eventStartDateEditText) }
        eventEndDateEditText.setOnClickListener { showDatePicker(eventEndDateEditText) }


        createEventButton.setOnClickListener { createEvent() }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format the date and set it to the EditText
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                editText.setText(dateFormat.format(selectedDate.time))
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun createEvent() {
        val eventName = eventNameEditText.text.toString()
        val eventDescription = eventDescriptionEditText.text.toString()
        val eventStartDate = eventStartDateEditText.text.toString()
        val eventEndDate = eventEndDateEditText.text.toString()
        val eventFeatures = eventFeaturesEditText.text.toString().split(",").map { it.trim() }

        if (eventName.isEmpty() || eventDescription.isEmpty() || eventStartDate.isEmpty() || eventEndDate.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(currentUser.uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Retrieve username and club from the user document
                    val username = document.getString("username") ?: ""
                    val club = document.getString("club") ?: ""


                    // Create event object with additional fields
                    val event = Event(
                        eventName = eventName,
                        eventDescription = eventDescription,
                        eventStartDate = eventStartDate,
                        eventEndDate = eventEndDate,
                        eventFeatures = eventFeatures,
                        addedBy = username.toString(),  // Store the username
                        club = club.toString()          // Store the club
                    )


                    db.collection("events").add(event)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error creating event: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "User document does not exist", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching user document: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}
