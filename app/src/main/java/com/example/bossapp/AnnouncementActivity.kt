package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.card.MaterialCardView

data class Announcement(
    val type: String,
    val title: String,
    val postDate: String,
    val department: String,
    val details: String,
    val deadlineDate: String
)

class AnnouncementActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var announcementsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annoucement)

        db = FirebaseFirestore.getInstance()
        announcementsContainer = findViewById(R.id.announcementsContainer)

        loadAnnouncements()

        findViewById<TextView>(R.id.createAnnouncementLink).setOnClickListener {
            startActivity(Intent(this, CreateAnnouncementActivity::class.java))
        }
    }

    private fun loadAnnouncements() {
        announcementsContainer.removeAllViews()

        db.collection("announcements").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val type = document.getString("type") ?: "No Type"
                    val title = document.getString("title") ?: "No Title"
                    val postDate = document.getString("postDate") ?: "No Post Date"
                    val department = document.getString("department") ?: "No Department"
                    val details = document.getString("details") ?: "No Details"
                    val deadlineDate = document.getString("deadlineDate") ?: "No Deadline Date"

                    val announcementView = createAnnouncementView(type, title, postDate, department, details, deadlineDate)
                    announcementsContainer.addView(announcementView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load announcements", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createAnnouncementView(type: String, title: String, postDate: String, department: String, details: String, deadlineDate: String): MaterialCardView {
        val cardView = MaterialCardView(this)
        cardView.setPadding(16, 16, 16, 16)
        cardView.radius = 16f
        cardView.cardElevation = 8f

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
        cardView.addView(textView)
        return cardView
    }
}
