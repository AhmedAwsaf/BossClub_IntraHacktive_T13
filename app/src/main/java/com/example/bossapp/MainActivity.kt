package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_menu)

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
    }
}
