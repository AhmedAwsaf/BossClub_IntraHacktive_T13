package com.example.bossapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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

        // Load and display user data
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
        // Navigate to different activities based on button clicks
        findViewById<ImageButton>(R.id.roomButton).setOnClickListener {
            navigateToActivity(BookedRoomActivity::class.java)
        }

        findViewById<ImageButton>(R.id.budgetButton).setOnClickListener {
            navigateToActivity(BudgetActivity::class.java)
        }

        findViewById<Button>(R.id.specialBtn).setOnClickListener {
            navigateToActivity(SpecialActivity::class.java)
        }

        findViewById<ImageButton>(R.id.eventButton).setOnClickListener {
            navigateToActivity(EventViewerActivity::class.java)
        }

        findViewById<ImageButton>(R.id.announcementButton).setOnClickListener {
            navigateToActivity(AnnouncementActivity::class.java)
        }

        findViewById<ImageButton>(R.id.communicateButton).setOnClickListener {
            navigateToActivity(CommunicationActivity::class.java)
        }

        // Handle survey button click with options dialog
        findViewById<ImageButton>(R.id.surveyButton).setOnClickListener {
            showSurveyOptionsDialog()
        }

        // Sign out button with confirmation
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
                    1 -> navigateToActivity(SurveyActivity::class.java)
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
                        val clr_level = document.getLong("clr_level")?.toInt() ?: 1
                        if(clr_level <= 1) {
                            Log.d("MainActivity", "clr_level: $clr_level")
                            findViewById<LinearLayout>(R.id.budgetLayout).visibility = View.GONE
                            findViewById<LinearLayout>(R.id.commLayout).visibility = View.GONE
                        } else {
                            findViewById<LinearLayout>(R.id.budgetLayout).visibility = View.VISIBLE
                            findViewById<LinearLayout>(R.id.commLayout).visibility = View.VISIBLE
                        }

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
