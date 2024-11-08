package com.example.bossapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_menu)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

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
        // Navigate to BookedRoomActivity
        findViewById<ImageButton>(R.id.roomButton).setOnClickListener {
            navigateToActivity(BookedRoomActivity::class.java)
        }

        // Navigate to BudgetActivity
        findViewById<ImageButton>(R.id.budgetButton).setOnClickListener {
            navigateToActivity(BudgetActivity::class.java)
        }

        // Sign Out Button with Confirmation
        findViewById<Button>(R.id.signoutBtn).setOnClickListener {
            showSignOutDialog()
        }
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
}
