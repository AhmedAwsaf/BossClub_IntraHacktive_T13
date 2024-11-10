package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SurveyActivity : AppCompatActivity() {

    private lateinit var surveyListLayout: LinearLayout
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        surveyListLayout = findViewById(R.id.surveyListLayout)
        findViewById<Button>(R.id.createSurveyButton).setOnClickListener {
            startActivity(Intent(this, CreateSurveyActivity::class.java))
        }

        loadSurveys()
    }

    private fun loadSurveys() {
        db.collection("surveys").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val surveyTitle = document.getString("title") ?: "No Title"
                    val surveyId = document.id

                    val surveyLink = TextView(this).apply {
                        text = surveyTitle
                        textSize = 18f
                        setOnClickListener { navigateToSolveSurvey(surveyId) }
                        setPadding(16, 16, 16, 16)
                        setBackgroundResource(R.drawable.card_border)
                    }
                    surveyListLayout.addView(surveyLink)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading surveys: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToSolveSurvey(surveyId: String) {
        val intent = Intent(this, SolveSurveyActivity::class.java)
        intent.putExtra("surveyId", surveyId)
        startActivity(intent)
    }
}
