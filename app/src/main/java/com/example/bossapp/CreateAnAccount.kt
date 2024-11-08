package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createaccount)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Set up the club Spinner
        val clubSpinner: Spinner = findViewById(R.id.clubSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.club_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            clubSpinner.adapter = adapter
        }

        // Other views and logic
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            val username = findViewById<EditText>(R.id.usernameEditText).text.toString()
            val phoneNumber = findViewById<EditText>(R.id.phoneNumberText).text.toString()
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val selectedClub = clubSpinner.selectedItem.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
            val semester = findViewById<EditText>(R.id.semesterEditText).text.toString()
            val department = findViewById<EditText>(R.id.departmentEditText).text.toString()

            // Validation for fields
            if (username.isNotEmpty() && phoneNumber.isNotEmpty() && email.isNotEmpty() &&
                selectedClub.isNotEmpty() && password.isNotEmpty() && semester.isNotEmpty() && department.isNotEmpty()
            ) {
                createUser(email, password, username, phoneNumber, semester, department, selectedClub)
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createUser(email: String, password: String, username: String, phoneNumber: String, semester: String, department: String, club: String) {
        // Check if the email has the correct domain
        if (email.endsWith("@g.bracu.ac.bd") || email.endsWith("@bracu.ac.bd")) {
            // Proceed with account creation if the email domain is valid
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Account creation successful
                        val user = auth.currentUser
                        user?.let {
                            saveUserDataToFirestore(it.uid, username, phoneNumber, semester, department, club)
                        }

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Account creation failed
                        Toast.makeText(this, "Account creation failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Show an error message if the email domain is incorrect
            Toast.makeText(this, "Please use a BRACU email address (@g.bracu.ac.bd or @bracu.ac.bd)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserDataToFirestore(userId: String, username: String, phoneNumber: String, semester: String, department: String, club: String) {
        val db = FirebaseFirestore.getInstance()

        // Define the custom data for the user
        val userData = hashMapOf(
            "username" to username,
            "phoneNumber" to phoneNumber,
            "semester" to semester,
            "department" to department,
            "club" to club,
            "profilePictureUrl" to "default.jpg" // Update this with an actual picture URL if available
        )

        // Save the custom user data in Firestore under the "users" collection
        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()

                // Navigate to LoginActivity upon successful account creation
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
