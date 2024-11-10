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


    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userlogin)


        auth = FirebaseAuth.getInstance()

        auth.addAuthStateListener { auth ->
            val user: FirebaseUser? = auth.currentUser

            if (user != null) {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }


        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.usernameEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        } else {

                            Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }


        val createAccountLink = findViewById<TextView>(R.id.createAccountLink)
        createAccountLink.setOnClickListener {

            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}
