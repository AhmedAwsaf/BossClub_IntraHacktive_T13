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


        adminUsernameEditText = findViewById(R.id.adminUsernameEditText)
        adminPasswordEditText = findViewById(R.id.adminPasswordEditText)
        adminLoginButton = findViewById(R.id.adminLoginButton)
        adminLoginImage = findViewById(R.id.adminLoginImage)


        adminLoginButton.setOnClickListener {

            val username = adminUsernameEditText.text.toString().trim()
            val password = adminPasswordEditText.text.toString().trim()


            if (isValidCredentials(username, password)) {

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {

                Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun isValidCredentials(username: String, password: String): Boolean {

        return username == "admin" && password == "password123"
    }
}
