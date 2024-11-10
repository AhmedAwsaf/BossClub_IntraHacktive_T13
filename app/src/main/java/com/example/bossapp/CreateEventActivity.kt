package com.example.bossapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_event_activity)

        // Initialize views
        eventNameEditText = findViewById(R.id.eventName)
        eventDescriptionEditText = findViewById(R.id.eventDescription)
        eventStartDateEditText = findViewById(R.id.eventStartDate)
        eventEndDateEditText = findViewById(R.id.eventEndDate)
        eventFeaturesEditText = findViewById(R.id.eventFeatures)
        createEventButton = findViewById(R.id.createEventButton)

        // Set up date pickers for start and end dates
        eventStartDateEditText.setOnClickListener { showDatePicker(eventStartDateEditText) }
        eventEndDateEditText.setOnClickListener { showDatePicker(eventEndDateEditText) }

        // Create event button click listener
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

        val event = Event(
            eventName = eventName,
            eventDescription = eventDescription,
            eventStartDate = eventStartDate,
            eventEndDate = eventEndDate,
            eventFeatures = eventFeatures
        )

        // Save event to Firestore
        db.collection("events").add(event)
            .addOnSuccessListener {
                Toast.makeText(this, "Event created successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error creating event: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
