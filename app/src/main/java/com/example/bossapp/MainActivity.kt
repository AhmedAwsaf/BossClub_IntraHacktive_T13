package com.example.bossapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

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

        // Check if the user is logged in, if not redirect to LoginActivity
        val currentUser = auth.currentUser
        if (currentUser == null) {
            redirectToLogin()
        }

        // Setup button click listeners
        setupButtonListeners()
    }

    // Function to handle button navigation
    private fun setupButtonListeners() {
        // Navigate to Booked Room Activity
        findViewById<ImageButton>(R.id.roomButton).setOnClickListener {
            navigateToActivity(BookedRoomActivity::class.java)
        }

        // Navigate to Budget Activity
        findViewById<ImageButton>(R.id.budgetButton).setOnClickListener {
            navigateToActivity(BudgetActivity::class.java)
        }

        // Special Panel Button for OCA users
        findViewById<Button>(R.id.specialBtn).setOnClickListener {
            navigateToActivity(SpecialActivity::class.java)
        }

        // Navigate to Event Creation
        findViewById<ImageButton>(R.id.eventButton).setOnClickListener {
            navigateToActivity(CreateEventActivity::class.java)
        }

        // Navigate to Announcement Activity
        findViewById<ImageButton>(R.id.announcementButton).setOnClickListener {
            navigateToActivity(AnnouncementActivity::class.java)
        }

        // Navigate to Communication Activity
        findViewById<ImageButton>(R.id.communicateButton).setOnClickListener {
            navigateToActivity(CommunicationActivity::class.java)
        }

        // Survey Button with options to Create or Solve Survey
        findViewById<ImageButton>(R.id.surveyButton).setOnClickListener {
            showSurveyOptionsDialog()
        }

        // Sign Out Button with Confirmation
        findViewById<Button>(R.id.signoutBtn).setOnClickListener {
            showSignOutDialog()
        }
    }

    // Show dialog for Survey options
    private fun showSurveyOptionsDialog() {
        val options = arrayOf("Create Survey", "Solve Survey")
        AlertDialog.Builder(this)
            .setTitle("Select an Option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> navigateToActivity(CreateSurveyActivity::class.java)
                    1 -> navigateToActivity(SolveSurveyActivity::class.java)
                }
            }
            .show()
    }

    // Generic function to navigate to a specified activity
    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    // Method to show sign-out confirmation dialog
    private fun showSignOutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
                redirectToLogin()
            }
            .setNegativeButton("No", null)
            .show()
    }

    // Method to redirect to LoginActivity
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Method to load user data and display it on the menu screen
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
                        val userType = document.getString("user_type") ?: ""

                        // Display user information
                        findViewById<TextView>(R.id.userNameText).text = username
                        findViewById<TextView>(R.id.clubNameText).text = "Club: $club"
                        findViewById<TextView>(R.id.club_deptText).text = "Department: $department"

                        // Show special panel button for OCA users
                        if (userType == "oca") {
                            findViewById<Button>(R.id.specialBtn).visibility = View.VISIBLE
                            findViewById<TextView>(R.id.clubNameText).text = "OCA"
                            findViewById<TextView>(R.id.club_deptText).text = "Admin"
                        }
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
