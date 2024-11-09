package com.example.bossapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SolveSurveyActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var responseLayout: LinearLayout
    private lateinit var submitResponsesButton: Button
    private val surveyId: String = "yourSurveyId" // Replace with actual survey ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solve_survey)

        db = Firebase.firestore

        // Initialize views
        responseLayout = findViewById(R.id.responseLayout)
        submitResponsesButton = findViewById(R.id.submitResponsesButton)

        // Load the survey questions
        loadSurveyQuestions()

        // Set up button click listener
        submitResponsesButton.setOnClickListener {
            submitSurveyResponses()
        }
    }

    private fun loadSurveyQuestions() {
        db.collection("surveys").document(surveyId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val title = document.getString("title") ?: "No Title"
                    val questions = document.get("questions") as? List<String> ?: listOf()

                    findViewById<TextView>(R.id.surveyTitleTextView).text = title

                    // Dynamically generate question fields
                    for (question in questions) {
                        addQuestionView(question)
                    }
                } else {
                    Toast.makeText(this, "Survey not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading survey: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addQuestionView(question: String) {
        val questionTextView = TextView(this)
        questionTextView.text = question
        questionTextView.textSize = 16f
        responseLayout.addView(questionTextView)

        val answerEditText = EditText(this)
        answerEditText.hint = "Enter your answer"
        responseLayout.addView(answerEditText)
    }

    private fun submitSurveyResponses() {
        val responses = mutableListOf<String>()
        for (i in 0 until responseLayout.childCount) {
            val view = responseLayout.getChildAt(i)
            if (view is EditText) {
                responses.add(view.text.toString())
            }
        }

        val responseMap = mapOf("responses" to responses)
        db.collection("responses").add(responseMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Responses submitted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error submitting responses: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
