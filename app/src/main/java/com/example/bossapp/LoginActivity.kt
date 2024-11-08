package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    // Firebase Auth instance
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userlogin)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        auth.addAuthStateListener { auth ->
            val user: FirebaseUser? = auth.currentUser

            if (user != null) {
                // User is signed in
                //Toast.makeText(this, "User signed in: ${user.email}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        // Set up the login button
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.usernameEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Sign in with Firebase Auth
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login successful
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                            // Navigate to the main activity or home screen
//                            val intent = Intent(this, MainActivity::class.java)
//                            startActivity(intent)
//                            finish() // Close login activity
                        } else {
                            // Login failed
                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up the admin login link
        val adminLoginLink = findViewById<TextView>(R.id.adminLoginLink)
        adminLoginLink.setOnClickListener {
            // Navigate to the Admin Login page
            val intent = Intent(this, AdminLoginActivity::class.java)
            startActivity(intent)
        }

        // Set up the create account link
        val createAccountLink = findViewById<TextView>(R.id.createAccountLink)
        createAccountLink.setOnClickListener {
            // Navigate to the Create Account page
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}
