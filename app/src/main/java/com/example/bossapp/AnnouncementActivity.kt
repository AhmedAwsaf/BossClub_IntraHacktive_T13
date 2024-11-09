package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore

data class Announcement(
    val type: String = "",
    val title: String = "",
    val postDate: String = "",
    val department: String = "",
    val details: String = "",
    val deadlineDate: String = ""
)

class AnnouncementActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var announcementsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annoucement)

        db = FirebaseFirestore.getInstance()
        announcementsContainer = findViewById(R.id.announcementsContainer)

        // Load announcements from Firestore
        loadAnnouncements()

        // Navigate to Create Announcement Activity
        findViewById<TextView>(R.id.createAnnouncementLink).setOnClickListener {
            startActivity(Intent(this, CreateAnnouncementActivity::class.java))
        }
    }

    private fun loadAnnouncements() {
        announcementsContainer.removeAllViews()

        // Fetch data from Firestore
        db.collection("announcements")
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("AnnouncementActivity", "No announcements found")
                } else {
                    Log.d("AnnouncementActivity", "Announcements found: ${documents.size()}")
                    for (document in documents) {
                        val type = document.getString("type") ?: "No Type"
                        val title = document.getString("title") ?: "No Title"
                        val postDate = document.getString("postDate") ?: "No Post Date"
                        val department = document.getString("department") ?: "No Department"
                        val details = document.getString("details") ?: "No Details"
                        val deadlineDate = document.getString("deadlineDate") ?: "No Deadline Date"

                        val cardView = createAnnouncementCard(
                            type, title, postDate, department, details, deadlineDate
                        )
                        announcementsContainer.addView(cardView)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AnnouncementActivity", "Error fetching announcements", exception)
                Toast.makeText(this, "Failed to load announcements", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createAnnouncementCard(
        type: String, title: String, postDate: String, department: String,
        details: String, deadlineDate: String
    ): CardView {
        val cardView = CardView(this)
        cardView.setContentPadding(16, 16, 16, 16)
        cardView.radius = 12f
        cardView.cardElevation = 8f
        cardView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 16, 0, 16)
        }

        val textView = TextView(this)
        textView.text = """
            Type: $type
            Title: $title
            Post Date: $postDate
            Department: $department
            Details: $details
            Deadline Date: $deadlineDate
        """.trimIndent()
        textView.textSize = 16f
        textView.setPadding(16, 16, 16, 16)

        cardView.addView(textView)
        return cardView
    }
}
