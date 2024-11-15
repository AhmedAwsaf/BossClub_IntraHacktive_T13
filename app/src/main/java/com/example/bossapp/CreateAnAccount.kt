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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createaccount)


        auth = FirebaseAuth.getInstance()


        val clubSpinner: Spinner = findViewById(R.id.clubSpinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.club_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            clubSpinner.adapter = adapter
        }


        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        createAccountButton.setOnClickListener {
            val username = findViewById<EditText>(R.id.usernameEditText).text.toString()
            val phoneNumber = findViewById<EditText>(R.id.phoneNumberText).text.toString()
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val selectedClub = clubSpinner.selectedItem.toString()
            val club_dept = findViewById<EditText>(R.id.club_deptEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
            val semester = findViewById<EditText>(R.id.semesterEditText).text.toString()
            val department = findViewById<EditText>(R.id.departmentEditText).text.toString()


            if (username.isNotEmpty() && phoneNumber.isNotEmpty() && email.isNotEmpty() &&
                selectedClub.isNotEmpty() && password.isNotEmpty() && semester.isNotEmpty() && department.isNotEmpty()
            ) {
                createUser(email, password, username, phoneNumber, semester, department, selectedClub, club_dept)
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createUser(email: String, password: String, username: String, phoneNumber: String, semester: String, department: String, club: String, clubdept:String) {

        if (email.endsWith("@g.bracu.ac.bd") || email.endsWith("@bracu.ac.bd")) {

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val user = auth.currentUser
                        user?.let {
                            saveUserDataToFirestore(it.uid, username, phoneNumber, semester, department, club, clubdept)
                        }

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {

                        Toast.makeText(this, "Account creation failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {

            Toast.makeText(this, "Please use a BRACU email address (@g.bracu.ac.bd or @bracu.ac.bd)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserDataToFirestore(userId: String, username: String, phoneNumber: String, semester: String, department: String, club: String, clubdept:String) {
        val db = FirebaseFirestore.getInstance()


        val userData = hashMapOf(
            "username" to username,
            "phoneNumber" to phoneNumber,
            "semester" to semester,
            "department" to department,
            "club" to club,
            "profilePictureUrl" to "default.jpg", // Update this with an actual picture URL if available
            "user_type" to "student",  // Dummy data for user type
            "club_role" to "General Member",  // Dummy data for club role
            "club_clr_level" to 1,  // Dummy data for club color level
            "club_dept" to clubdept, // Dummy data for club department
            "created_at" to FieldValue.serverTimestamp()
        )


        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()


                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error adding user data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
