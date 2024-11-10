package com.example.bossapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class BudgetStatusActivity : AppCompatActivity() {

    private lateinit var budgetRequestContainer: LinearLayout
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.budget_status)

        budgetRequestContainer = findViewById(R.id.budgetRequestContainer)

        fetchAndDisplayBudgetRequests()
    }

    private fun fetchAndDisplayBudgetRequests() {
        db.collection("budget_requests").get()
            .addOnSuccessListener { result ->
                for (document in result) {

                    val value = document.getString("value") ?: "N/A"
                    val reason = document.getString("reason") ?: "N/A"
                    val description = document.getString("description") ?: "N/A"
                    val spendAmount = document.getString("spendAmount") ?: "N/A"
                    val eventName = document.getString("eventName") ?: "N/A"
                    val approved = document.getBoolean("approved") ?: false


                    val requestContainer = LinearLayout(this).apply {
                        orientation = LinearLayout.VERTICAL
                        setPadding(32, 32, 32, 32)
                        setBackgroundResource(R.drawable.spinner_border)
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        params.setMargins(0, 16, 0, 16)
                        layoutParams = params
                    }


                    fun addDetail(label: String, value: String) {
                        val textView = TextView(this).apply {
                            text = "$label: $value"
                            textSize = 16f
                            setPadding(8, 4, 8, 4)
                            setTextColor(Color.DKGRAY)
                        }
                        requestContainer.addView(textView)
                    }


                    addDetail("Value", value)
                    addDetail("Reason", reason)
                    addDetail("Description", description)
                    addDetail("Spend Amount", spendAmount)
                    addDetail("Event Name", eventName)


                    val approvedStatus = TextView(this).apply {
                        text = if (approved) "Approved" else "Not Approved"
                        textSize = 18f
                        setPadding(8, 8, 8, 8)
                        setTextColor(if (approved) Color.GREEN else Color.RED)
                        setTypeface(null, Typeface.BOLD)
                        gravity = Gravity.CENTER_HORIZONTAL
                    }
                    requestContainer.addView(approvedStatus)


                    budgetRequestContainer.addView(requestContainer)
                }
            }
            .addOnFailureListener { exception ->

                val errorTextView = TextView(this).apply {
                    text = "Failed to load budget requests: ${exception.message}"
                    setTextColor(Color.RED)
                    setPadding(8, 8, 8, 8)
                }
                budgetRequestContainer.addView(errorTextView)
            }
    }
}
