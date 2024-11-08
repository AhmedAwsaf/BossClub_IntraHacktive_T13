package com.example.bossapp


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_menu)

        // Set up button click listeners
        findViewById<ImageButton>(R.id.eventButton).setOnClickListener {
            // TODO: Navigate to the Event activity
            // startActivity(Intent(this, EventActivity::class.java))
        }

        findViewById<ImageButton>(R.id.budgetButton).setOnClickListener {
            // TODO: Navigate to the Budget activity
            // startActivity(Intent(this, BudgetActivity::class.java))
        }

        findViewById<ImageButton>(R.id.roomButton).setOnClickListener {
            // TODO: Navigate to the Room activity
            // startActivity(Intent(this, RoomActivity::class.java))
        }

        findViewById<ImageButton>(R.id.announcementButton).setOnClickListener {
            // TODO: Navigate to the Announcement activity
            // startActivity(Intent(this, AnnouncementActivity::class.java))
        }

        findViewById<ImageButton>(R.id.surveyButton).setOnClickListener {
            // TODO: Navigate to the Survey activity
            // startActivity(Intent(this, SurveyActivity::class.java))
        }

        findViewById<ImageButton>(R.id.communicateButton).setOnClickListener {
            // TODO: Navigate to the Communicate activity
            // startActivity(Intent(this, CommunicateActivity::class.java))
        }
    }
}
