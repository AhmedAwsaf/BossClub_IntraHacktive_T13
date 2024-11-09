package com.example.bossapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class CreateSurveyActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
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
        val questionEditText = EditText(this)
        questionEditText.hint = "Enter Question"
        questionEditText.textSize = 16f
        questionsLayout.addView(questionEditText)
    }

    private fun getQuestions(): List<String> {
        val questions = mutableListOf<String>()
        for (i in 0 until questionsLayout.childCount) {
            val questionField = questionsLayout.getChildAt(i) as EditText
            val question = questionField.text.toString()
            if (question.isNotEmpty()) {
                questions.add(question)
            }
        }
        return questions
    }

    private fun saveSurveyToFirestore(title: String, questions: List<String>) {
        val surveyData = hashMapOf(
            "title" to title,
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
