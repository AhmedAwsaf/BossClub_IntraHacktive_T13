package com.example.bossapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MenuActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_menu)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Fetch user data and display it
        loadUserData()

        // Set up button click listeners
        findViewById<ImageButton>(R.id.eventButton).setOnClickListener {
            // TODO: Navigate to the Event activity
        }

        findViewById<ImageButton>(R.id.budgetButton).setOnClickListener {
            // TODO: Navigate to the Budget activity
        }

        findViewById<ImageButton>(R.id.roomButton).setOnClickListener {
            // TODO: Navigate to the Room activity
        }

        findViewById<ImageButton>(R.id.announcementButton).setOnClickListener {
            // TODO: Navigate to the Announcement activity
        }

        findViewById<ImageButton>(R.id.surveyButton).setOnClickListener {
            // TODO: Navigate to the Survey activity
        }

        findViewById<ImageButton>(R.id.communicateButton).setOnClickListener {
            // TODO: Navigate to the Communicate activity
        }
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username") ?: ""
                        val club = document.getString("club") ?: ""
                        val department = document.getString("department") ?: ""
                        val clubRole = document.getString("club_role") ?: "Member"

                        // Display data on the menu screen
                        findViewById<TextView>(R.id.usernameTextView).text = username
                        findViewById<TextView>(R.id.clubTextView).text = "Club: $club"
                        findViewById<TextView>(R.id.departmentTextView).text = "Department: $department"
                        findViewById<TextView>(R.id.clubRoleTextView).text = "Role: $clubRole"
                    } else {
                        Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
