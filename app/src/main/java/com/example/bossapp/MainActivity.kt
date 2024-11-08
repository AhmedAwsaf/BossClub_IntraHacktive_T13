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
        val currentUser = auth.currentUser

        // Check if the user is logged in
        if (currentUser == null) {
            // If not logged in, redirect to LoginActivity
            redirectToLogin()
        }

        // Set up button click listeners
        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        // Room Booking Button - navigate to BookedRoomActivity
        findViewById<ImageButton>(R.id.roomButton).setOnClickListener {
            startActivity(Intent(this, BookedRoomActivity::class.java))
        }

        // Sign Out Button with Confirmation
        findViewById<Button>(R.id.signoutBtn).setOnClickListener {
            showSignOutDialog()
        }
    }

    // Method to show sign-out confirmation dialog
    private fun showSignOutDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign Out")
        builder.setMessage("Are you sure you want to sign out?")
        builder.setPositiveButton("Yes") { _, _ ->
            auth.signOut()
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
            redirectToLogin()
        }
        builder.setNegativeButton("No", null)
        builder.show()
    }

    // Method to redirect to LoginActivity
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
