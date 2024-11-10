package com.example.bossapp

import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreateSurveyActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var questionsLayout: LinearLayout
    private lateinit var surveyTitleEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_survey)

        surveyTitleEditText = findViewById(R.id.surveyTitleEditText)
        questionsLayout = findViewById(R.id.questionsLayout)
        val addQuestionButton: Button = findViewById(R.id.addQuestionButton)
        val submitSurveyButton: Button = findViewById(R.id.submitSurveyButton)

        addQuestionButton.setOnClickListener { addQuestionField() }

        submitSurveyButton.setOnClickListener {
            val title = surveyTitleEditText.text.toString()
            val questions = getQuestions()

            if (title.isEmpty() || questions.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveSurveyToFirestore(title, questions)
        }
    }

    private fun addQuestionField() {
        // Create a layout to hold the question field and the type spinner
        val questionLayout = LinearLayout(this)
        questionLayout.orientation = LinearLayout.HORIZONTAL
        questionLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Create the question EditText
        val questionEditText = EditText(this)
        questionEditText.hint = "Enter Question"
        questionEditText.textSize = 16f
        questionEditText.layoutParams = LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1f
        )

        // Create the question type spinner
        val typeSpinner = Spinner(this)
        val types = arrayOf("Sentence", "True/False", "Out of 5")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = spinnerAdapter

        // Add question EditText and type spinner to the question layout
        questionLayout.addView(questionEditText)
        questionLayout.addView(typeSpinner)

        // Add the question layout to the main questions layout
        questionsLayout.addView(questionLayout)
    }

    private fun getQuestions(): List<Map<String, String>> {
        val questions = mutableListOf<Map<String, String>>()
        for (i in 0 until questionsLayout.childCount) {
            val questionLayout = questionsLayout.getChildAt(i) as LinearLayout
            val questionField = questionLayout.getChildAt(0) as EditText
            val typeSpinner = questionLayout.getChildAt(1) as Spinner

            val questionText = questionField.text.toString()
            val questionType = typeSpinner.selectedItem.toString()

            if (questionText.isNotEmpty()) {
                questions.add(
                    mapOf(
                        "question" to questionText,
                        "type" to questionType
                    )
                )
            }
        }
        return questions
    }

    private fun saveSurveyToFirestore(title: String, questions: List<Map<String, String>>) {
        val surveyData = hashMapOf(
            "title" to title,
            "createdBy" to auth.currentUser?.uid,
            "questions" to questions
        )

        db.collection("surveys").add(surveyData)
            .addOnSuccessListener {
                Toast.makeText(this, "Survey Created Successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
