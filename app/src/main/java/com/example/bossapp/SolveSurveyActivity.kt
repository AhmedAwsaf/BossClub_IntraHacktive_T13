package com.example.bossapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SolveSurveyActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var responseLayout: LinearLayout
    private lateinit var submitResponsesButton: Button
    private lateinit var surveyId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solve_survey)

        db = Firebase.firestore

        // Retrieve the survey ID from the intent and check if it's null
        surveyId = intent.getStringExtra("surveyId") ?: ""
        if (surveyId.isEmpty()) {
            Toast.makeText(this, "Survey ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

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
                    val questions = document.get("questions") as? List<Map<String, String>> ?: listOf()

                    findViewById<TextView>(R.id.surveyTitleTextView).text = title

                    // Dynamically generate question fields based on type
                    for (questionData in questions) {
                        val questionText = questionData["question"] ?: "Untitled Question"
                        val questionType = questionData["type"] ?: "Sentence"
                        addQuestionView(questionText, questionType)
                    }
                } else {
                    Toast.makeText(this, "Survey not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading survey: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addQuestionView(questionText: String, questionType: String) {
        val questionTextView = TextView(this).apply {
            text = questionText
            textSize = 16f
        }
        responseLayout.addView(questionTextView)

        when (questionType) {
            "Sentence" -> {
                val answerEditText = EditText(this).apply {
                    hint = "Enter your answer"
                }
                responseLayout.addView(answerEditText)
            }
            "True/False" -> {
                val optionsGroup = RadioGroup(this)
                val trueOption = RadioButton(this).apply { text = "True" }
                val falseOption = RadioButton(this).apply { text = "False" }
                optionsGroup.addView(trueOption)
                optionsGroup.addView(falseOption)
                responseLayout.addView(optionsGroup)
            }
            "Out of 5" -> {
                val ratingBar = RatingBar(this).apply {
                    numStars = 5
                    stepSize = 1f
                    rating = 0f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 8, 0, 16)
                    }
                }
                responseLayout.addView(ratingBar)
            }
            else -> {
                // Default to sentence type if the type is unrecognized
                val answerEditText = EditText(this).apply {
                    hint = "Enter your answer"
                }
                responseLayout.addView(answerEditText)
            }
        }
    }

    private fun submitSurveyResponses() {
        val responses = mutableListOf<Map<String, Any>>()
        var questionIndex = 0

        // Iterate through responseLayout children to collect answers
        for (i in 0 until responseLayout.childCount) {
            val view = responseLayout.getChildAt(i)
            val responseMap = mutableMapOf<String, Any>()

            when (view) {
                is EditText -> {
                    responseMap["answer"] = view.text.toString()
                    responses.add(responseMap)
                }
                is RadioGroup -> {
                    val selectedOption = findViewById<RadioButton>(view.checkedRadioButtonId)
                    responseMap["answer"] = selectedOption?.text.toString()
                    responses.add(responseMap)
                }
                is RatingBar -> {
                    responseMap["answer"] = view.rating.toInt()
                    responses.add(responseMap)
                }
            }
            questionIndex++
        }

        // Save responses to Firestore
        val responseMap = mapOf(
            "surveyId" to surveyId,
            "responses" to responses
        )

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
