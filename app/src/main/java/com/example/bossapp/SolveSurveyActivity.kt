package com.example.bossapp

import android.graphics.Typeface
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


        surveyId = intent.getStringExtra("surveyId") ?: ""
        if (surveyId.isEmpty()) {
            Toast.makeText(this, "Survey ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        responseLayout = findViewById(R.id.responseLayout)
        submitResponsesButton = findViewById(R.id.submitResponsesButton)


        loadSurveyQuestions()


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
        val context = this

        val questionTextView = TextView(context).apply {
            text = questionText
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setPadding(16, 16, 16, 8)
        }
        responseLayout.addView(questionTextView)

        when (questionType) {
            "Sentence" -> {
                val answerEditText = EditText(context).apply {
                    hint = "Enter your answer"
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 8, 0, 16)
                    }
                }
                responseLayout.addView(answerEditText)
            }
            "True/False" -> {
                val optionsGroup = RadioGroup(context).apply {
                    orientation = RadioGroup.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 8, 0, 16)
                    }
                }

                val trueOption = RadioButton(context).apply {
                    text = "True"
                    textSize = 16f
                    setPadding(8, 8, 8, 8)
                }
                val falseOption = RadioButton(context).apply {
                    text = "False"
                    textSize = 16f
                    setPadding(8, 8, 8, 8)
                }

                optionsGroup.addView(trueOption)
                optionsGroup.addView(falseOption)
                responseLayout.addView(optionsGroup)
            }
            "Out of 5" -> {
                val ratingBar = RatingBar(context).apply {
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
                val answerEditText = EditText(context).apply {
                    hint = "Enter your answer"
                    textSize = 16f
                    setPadding(16, 8, 16, 8)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 8, 0, 16)
                    }
                }
                responseLayout.addView(answerEditText)
            }
        }
    }



    private fun submitSurveyResponses() {
        val responses = mutableListOf<Map<String, Any>>()
        var questionIndex = 0


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
