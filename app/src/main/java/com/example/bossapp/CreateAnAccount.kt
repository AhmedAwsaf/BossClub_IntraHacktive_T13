package com.example.bossapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createaccount)

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
            val fullName = findViewById<EditText>(R.id.fullNameEditText).text.toString()
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val role = findViewById<EditText>(R.id.roleEditText).text.toString()
            val selectedClub = clubSpinner.selectedItem.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
            val department = findViewById<EditText>(R.id.departmentEditText).text.toString()
            val semester = findViewById<EditText>(R.id.semesterEditText).text.toString()

            // Implement validation and account creation logic here
            if (username.isNotEmpty() && fullName.isNotEmpty() && email.isNotEmpty() &&
                role.isNotEmpty() && password.isNotEmpty() && department.isNotEmpty() && semester.isNotEmpty()
            ) {
                Toast.makeText(this, "Account created for $username in club $selectedClub", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
