package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AdminLoginActivity : AppCompatActivity() {

    private lateinit var adminUsernameEditText: EditText
    private lateinit var adminPasswordEditText: EditText
    private lateinit var adminLoginButton: Button
    private lateinit var adminLoginImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adminlogin)

        // Initialize views
        adminUsernameEditText = findViewById(R.id.adminUsernameEditText)
        adminPasswordEditText = findViewById(R.id.adminPasswordEditText)
        adminLoginButton = findViewById(R.id.adminLoginButton)
        adminLoginImage = findViewById(R.id.adminLoginImage)

        // Set up the login button click listener
        adminLoginButton.setOnClickListener {
            // Get the entered username and password
            val username = adminUsernameEditText.text.toString().trim()
            val password = adminPasswordEditText.text.toString().trim()

            // Check if credentials are valid (replace with actual validation logic)
            if (isValidCredentials(username, password)) {
                // Navigate to the admin dashboard or another screen
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Optional: closes the login screen so user cannot go back
            } else {
                // Show an error message
                Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to validate credentials (replace with actual validation logic)
    private fun isValidCredentials(username: String, password: String): Boolean {
        // Simple hardcoded check for demonstration (replace with actual validation)
        return username == "admin" && password == "password123"
    }
}
