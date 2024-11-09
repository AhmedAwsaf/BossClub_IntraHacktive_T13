package com.example.bossapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CreateAnnouncementActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_annoucement)

        db = FirebaseFirestore.getInstance()

        val typeEditText: EditText = findViewById(R.id.typeEditText)
        val titleEditText: EditText = findViewById(R.id.titleEditText)
        val departmentEditText: EditText = findViewById(R.id.departmentEditText)
        val detailsEditText: EditText = findViewById(R.id.detailsEditText)
        val deadlineDateEditText: EditText = findViewById(R.id.deadlineDateEditText)
        val submitButton: Button = findViewById(R.id.submitAnnouncementButton)

        val postDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        submitButton.setOnClickListener {
            val type = typeEditText.text.toString()
            val title = titleEditText.text.toString()
            val department = departmentEditText.text.toString()
            val details = detailsEditText.text.toString()
            val deadlineDate = deadlineDateEditText.text.toString()

            if (type.isNotEmpty() && title.isNotEmpty() && department.isNotEmpty() && details.isNotEmpty() && deadlineDate.isNotEmpty()) {
                saveAnnouncement(type, title, postDate, department, details, deadlineDate)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveAnnouncement(type: String, title: String, postDate: String, department: String, details: String, deadlineDate: String) {
        val announcement = hashMapOf(
            "type" to type,
            "title" to title,
            "postDate" to postDate,
            "department" to department,
            "details" to details,
            "deadlineDate" to deadlineDate
        )

        db.collection("announcements").add(announcement)
            .addOnSuccessListener {
                Toast.makeText(this, "Announcement created successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create announcement", Toast.LENGTH_SHORT).show()
            }
    }
}
