package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_menu)

        val auth = FirebaseAuth.getInstance()
        


        // Set up button click listeners
        findViewById<ImageButton>(R.id.eventButton).setOnClickListener {
            // Navigate to Event activity
            // startActivity(Intent(this, EventActivity::class.java))
        }

        findViewById<ImageButton>(R.id.budgetButton).setOnClickListener {
            // Navigate to Budget activity
            // startActivity(Intent(this, BudgetActivity::class.java))
        }

        findViewById<ImageButton>(R.id.roomButton).setOnClickListener {
            // Navigate to Room booking activity
            startActivity(Intent(this, RoomActivity::class.java))
        }

        findViewById<ImageButton>(R.id.announcementButton).setOnClickListener {
            // Navigate to Announcement activity
            // startActivity(Intent(this, AnnouncementActivity::class.java))
        }

        findViewById<ImageButton>(R.id.surveyButton).setOnClickListener {
            // Navigate to Survey activity
            // startActivity(Intent(this, SurveyActivity::class.java))
        }

        findViewById<ImageButton>(R.id.communicateButton).setOnClickListener {
            // Navigate to Communicate activity
            // startActivity(Intent(this, CommunicateActivity::class.java))
        }

        findViewById<Button>(R.id.signoutBtn).setOnClickListener {
            auth.signOut()

            // Optionally, show a toast message
            Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()

            // Navigate to login activity after signing out
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
