package com.example.bossapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


data class BudgetRequest(
    val userId: String = "",
    val value: String = "",
    val reason: String = "",
    val description: String = "",
    val spendAmount: String = "",
    val eventName: String = "",
    val approved: Boolean = false
)

class SpecialActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var linearLayoutButtons: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.special_panel)

        db = FirebaseFirestore.getInstance()

        linearLayoutButtons = findViewById<LinearLayout>(R.id.linearLayoutButtons)

        findViewById<Button>(R.id.button).setOnClickListener{
            val intent = Intent(this, OverviewActivity::class.java)
            startActivity(intent)
        }

        fetchEvents()

        fetchBudgets()
    }

    private fun addTextViewToLinearLayout(text: String) {
        // Create a new TextView
        val textView = TextView(this)

        // Set the text for the TextView
        textView.text = text

        // Optionally, you can style the TextView (size, padding, etc.)
        textView.textSize = 16f  // Set text size to 16sp
        textView.setPadding(16, 16, 16, 16)  // Set padding for the TextView

        // Add the TextView to the LinearLayout
        linearLayoutButtons.addView(textView)
    }

    private fun fetchEvents() {
        db.collection("events")
            .whereEqualTo("status", "waiting")
            .get()
            .addOnSuccessListener { documents ->
                addTextViewToLinearLayout("Waiting for approval events")
                for (document in documents) {
                    val event = document.toObject(Event::class.java)
                    createEventButton(event.eventName, event, document.id)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching events: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchBudgets(){
        db.collection("budget_requests")
            .whereEqualTo("approved", false)
            .get()
            .addOnSuccessListener { documents ->
                addTextViewToLinearLayout("Waiting for approval budgets")
                for (document in documents) {
                    val budgetRequest = document.toObject(BudgetRequest::class.java)
                    createBudgetRequestButton(budgetRequest.reason, budgetRequest, document.id)
                }

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching budgets: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createEventButton(name: String, event: Event, eventId: String ) {
        val button = Button(this)
        button.text = name + " - Event"

        // Optionally, set button properties, e.g., padding, text size, background color
        button.textSize = 16f
        button.setPadding(16, 16, 16, 16)

        // Set an OnClickListener if needed
        button.setOnClickListener {
            showEventDialog(event.eventName, event, eventId)
        }

        // Add the button to the LinearLayout
        linearLayoutButtons.addView(button)
    }

    private fun createBudgetRequestButton(name: String, budgetRequest: BudgetRequest, budgetRequestId: String ) {
        val button = Button(this)
        button.text = name + " - Budget Request"

        // Optionally, set button properties, e.g., padding, text size, background color
        button.textSize = 16f
        button.setPadding(16, 16, 16, 16)

        // Set an OnClickListener if needed
        button.setOnClickListener {
            showBudgetDialog(budgetRequest.reason, budgetRequest, budgetRequestId)
        }

        // Add the button to the LinearLayout
        linearLayoutButtons.addView(button)
    }

    private fun showEventDialog(title: String, event: Event, eventId: String) {
        val eventDetails = formatEventDetails(event)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(eventDetails)
            .setPositiveButton("Approve") { dialog, _ ->
                // Handle the approve action
                Toast.makeText(this, "$title approved", Toast.LENGTH_SHORT).show()
                updateEventStatus(eventId, "approved")
                dialog.dismiss()
            }
            .setNegativeButton("Reject") { dialog, _ ->
                // Handle the reject action
                Toast.makeText(this, "$title rejected", Toast.LENGTH_SHORT).show()
                updateEventStatus(eventId, "rejected")
                dialog.dismiss()
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                Toast.makeText(this, "Action canceled", Toast.LENGTH_SHORT).show()
                dialog.dismiss()  // Just dismiss the dialog without making any changes
            }
            .create()
            .show()
    }

    private fun showBudgetDialog(title: String, budgetRequest: BudgetRequest, budgetRequestId: String) {
        val budgetDetails = formatBudgetRequestDetails(budgetRequest)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(budgetDetails)
            .setPositiveButton("Approve") { dialog, _ ->
                // Handle the approve action
                Toast.makeText(this, "$title approved", Toast.LENGTH_SHORT).show()
                updateBudgetStatus(budgetRequestId, true)
                dialog.dismiss()
            }
            .setNeutralButton("Cancel") { dialog, _ ->
                Toast.makeText(this, "Action canceled", Toast.LENGTH_SHORT).show()
                dialog.dismiss()  // Just dismiss the dialog without making any changes
            }
            .create()
            .show()
    }

    private fun formatEventDetails(event: Event): String {
        return """
        Description: ${event.eventDescription}
        Start Date: ${event.eventStartDate}
        End Date: ${event.eventEndDate}
        Features: ${event.eventFeatures.joinToString(", ")}
        Status: ${event.status}
        Signed By: ${event.signedBy}
        Club: ${event.club}
        Added By: ${event.addedBy}
        """.trimIndent()
    }

    private fun formatBudgetRequestDetails(budgetRequest: BudgetRequest): String {
        return """
        Description: ${budgetRequest.description}
        
        Event Name: ${budgetRequest.eventName}
        Total Budget: ${budgetRequest.value}
        Approved: ${budgetRequest.approved}
        
        Already Spent Amount: ${budgetRequest.spendAmount}
    """.trimIndent()
    }

    private fun updateEventStatus(eventId: String, status: String) {
        db.collection("events").document(eventId)
            .update("status", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Event status updated to $status", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateBudgetStatus(budgetRequestId: String, status: Boolean) {
        db.collection("budget_requests").document(budgetRequestId)
            .update("approved", status)
            .addOnSuccessListener {
                Toast.makeText(this, "Budget Request status updated to $status", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to update status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

