package com.example.bossapp

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
        val clubEditText = findViewById<EditText>(R.id.clubNameEditText)
        val detailsEditText = findViewById<EditText>(R.id.detailsEditText)
        val deadlineDateEditText = findViewById<EditText>(R.id.deadlineDateEditText)
        val submitButton = findViewById<Button>(R.id.submitAnnouncementButton)

        submitButton.setOnClickListener {
            val type = typeEditText.text.toString()
            val title = titleEditText.text.toString()
            val club = clubEditText.text.toString()
            val details = detailsEditText.text.toString()
            val deadlineDate = deadlineDateEditText.text.toString()
            val currentUser = auth.currentUser

            if (currentUser != null) {
                // Fetch the username of the logged-in user
                db.collection("users").document(currentUser.uid).get()
                    .addOnSuccessListener { document ->
                        val username = document.getString("username") ?: "Anonymous"

                        val postDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                        val announcementData = mapOf(
                            "type" to type,
                            "title" to title,
                            "club" to club,
                            "details" to details,
                            "postDate" to postDate,
                            "deadlineDate" to deadlineDate,
                            "postedBy" to username // Save the username here
                        )

                        db.collection("announcements").add(announcementData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Announcement created successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
        }
    }
}
