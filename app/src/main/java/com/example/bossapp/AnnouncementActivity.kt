package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AnnouncementActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var announcementsContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annoucement)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        announcementsContainer = findViewById(R.id.announcementsContainer)

        findViewById<TextView>(R.id.createAnnouncementLink).setOnClickListener {
            startActivity(Intent(this, CreateAnnouncementActivity::class.java))
        }

        loadAnnouncements()

        showAnnounceButton()

    }

    private fun showAnnounceButton(){
        val currentUser = auth.currentUser
        val userId = currentUser?.uid ?: ""
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val clr_level = document.getLong("club_clr_level")?.toInt() ?: 1
                    if(clr_level <= 1) {
                        findViewById<TextView>(R.id.createAnnouncementLink).visibility = View.GONE
                    } else {
                        findViewById<TextView>(R.id.createAnnouncementLink).visibility = View.VISIBLE
                    }
                }
                else {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

    }

    private fun loadAnnouncements() {
        announcementsContainer.removeAllViews()

        db.collection("announcements")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val type = document.getString("type") ?: "No Type"
                    val title = document.getString("title") ?: "No Title"
                    val postDate = document.getString("postDate") ?: "No Post Date"
                    val club = document.getString("club") ?: "No Club"
                    val details = document.getString("details") ?: "No Details"
                    val deadlineDate = document.getString("deadlineDate") ?: "No Deadline"
                    val postedBy = document.getString("postedBy") ?: "Anonymous"

                    val cardView = createAnnouncementCard(
                        type, title, postDate, club, details, deadlineDate, postedBy
                    )
                    announcementsContainer.addView(cardView)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load announcements", Toast.LENGTH_SHORT).show()
                Log.e("AnnouncementActivity", "Error fetching announcements", exception)
            }
    }

    private fun createAnnouncementCard(
        type: String, title: String, postDate: String, club: String,
        details: String, deadlineDate: String, postedBy: String
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
            Posted on: $postDate
            Club: $club
            Details: $details
            Deadline: $deadlineDate
            By: $postedBy
        """.trimIndent()
        textView.textSize = 16f
        textView.setPadding(16, 16, 16, 16)

        cardView.addView(textView)
        return cardView
    }
}
