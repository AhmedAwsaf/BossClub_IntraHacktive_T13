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

class CreateAnnouncementActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_annoucement)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val typeEditText = findViewById<EditText>(R.id.typeEditText)
        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val clubNameEditText = findViewById<EditText>(R.id.clubNameEditText)
        val detailsEditText = findViewById<EditText>(R.id.detailsEditText)
        val deadlineDateEditText = findViewById<EditText>(R.id.deadlineDateEditText)
        val submitButton = findViewById<Button>(R.id.submitAnnouncementButton)

        // Handle date picker
        deadlineDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, { _, year, month, day ->
                val formattedDate = "$year-${month + 1}-$day"
                deadlineDateEditText.setText(formattedDate)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            datePicker.show()
        }

        // Handle form submission
        submitButton.setOnClickListener {
            val type = typeEditText.text.toString()
            val title = titleEditText.text.toString()
            val clubName = clubNameEditText.text.toString()
            val details = detailsEditText.text.toString()
            val deadlineDate = deadlineDateEditText.text.toString()
            val username = auth.currentUser?.displayName ?: "Anonymous"

            if (type.isNotEmpty() && title.isNotEmpty() && clubName.isNotEmpty() && details.isNotEmpty() && deadlineDate.isNotEmpty()) {
                val announcement = mapOf(
                    "type" to type,
                    "title" to title,
                    "clubName" to clubName,
                    "details" to details,
                    "deadlineDate" to deadlineDate,
                    "username" to username,
                    "postDate" to SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                )

                db.collection("announcements").add(announcement)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Announcement created!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to create announcement: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
