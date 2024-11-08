package com.example.bossapp


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BudgetActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Set up form elements
        val valueEditText: EditText = findViewById(R.id.valueEditText)
        val reasonEditText: EditText = findViewById(R.id.reasonEditText)
        val descriptionEditText: EditText = findViewById(R.id.descriptionEditText)
        val spendAmountEditText: EditText = findViewById(R.id.spendAmountEditText)
        val submitButton: Button = findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val value = valueEditText.text.toString()
            val reason = reasonEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val spendAmount = spendAmountEditText.text.toString()

            if (value.isNotEmpty() && reason.isNotEmpty() && description.isNotEmpty() && spendAmount.isNotEmpty()) {
                saveBudgetRequest(value, reason, description, spendAmount)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBudgetRequest(value: String, reason: String, description: String, spendAmount: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val budgetRequest = hashMapOf(
                "userId" to currentUser.uid,
                "value" to value,
                "reason" to reason,
                "description" to description,
                "spendAmount" to spendAmount
            )

            db.collection("budget_requests").add(budgetRequest)
                .addOnSuccessListener {
                    Toast.makeText(this, "Budget request submitted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to submit request: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
