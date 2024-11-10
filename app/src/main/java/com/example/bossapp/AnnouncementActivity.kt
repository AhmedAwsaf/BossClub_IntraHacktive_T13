package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.bossapp.R

data class Announcement(
    val type: String = "",
    val title: String = "",
    val postDate: String = "",
    val clubName: String = "",
    val details: String = "",
    val deadlineDate: String = "",
    val username: String = ""
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
                        val clubName = document.getString("clubName") ?: "No Club"
                        val details = document.getString("details") ?: "No Details"
                        val deadlineDate = document.getString("deadlineDate") ?: "No Deadline Date"
                        val username = document.getString("username") ?: "Anonymous"

                        val cardView = createAnnouncementCard(
                            type, title, postDate, clubName, details, deadlineDate, username
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
        type: String,
        title: String,
        postDate: String,
        clubName: String,
        details: String,
        deadlineDate: String,
        username: String
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

        val contentView = LayoutInflater.from(this).inflate(R.layout.item_announcement, null)

        // Set announcement details
        val typeTextView = contentView.findViewById<TextView>(R.id.typeText)
        val titleTextView = contentView.findViewById<TextView>(R.id.titleText)
        val postDateTextView = contentView.findViewById<TextView>(R.id.postDateText)
        val clubNameTextView = contentView.findViewById<TextView>(R.id.clubNameText)
        val detailsTextView = contentView.findViewById<TextView>(R.id.detailsText)
        val deadlineDateTextView = contentView.findViewById<TextView>(R.id.deadlineDateText)
        val usernameTextView = contentView.findViewById<TextView>(R.id.usernameText)

        typeTextView.text = "Type: $type"
        titleTextView.text = "Title: $title"
        postDateTextView.text = "Post Date: $postDate"
        clubNameTextView.text = "Club: $clubName"
        detailsTextView.text = "Details: $details"
        deadlineDateTextView.text = "Deadline: $deadlineDate"
        usernameTextView.text = "by *$username*"

        cardView.addView(contentView)
        return cardView
    }
}
