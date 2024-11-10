package com.example.bossapp

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner

data class User(
    val username: String = "",
    val phoneNumber: String = "",
    val semester: String = "",
    val department: String = "",
    val club: String = "",
    val profilePictureUrl: String = "default.jpg",
    val user_type: String = "student",
    val club_role: String = "General Member",
    val club_clr_level: Int = 1,
    val club_dept: String = "",
    val created_at: Any? = null
)



class OverviewActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var linearLayoutContainer: LinearLayout

    private lateinit var dataTypeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        db = FirebaseFirestore.getInstance()
        linearLayoutContainer = findViewById(R.id.linearLayoutContainer)
        dataTypeSpinner = findViewById(R.id.dataTypeSpinner)
        val dataTypes = listOf("Users", "Events", "Budget Requests", "Room Bookings")


        // Set up spinner with options
        val adapter = ArrayAdapter(this, R.layout.spinner_item, dataTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dataTypeSpinner.adapter = adapter

        // Fetch data when a new option is selected
        dataTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                linearLayoutContainer.removeAllViews() // Clear previous views
                when (dataTypes[position]) {
                    "Users" -> fetchUsers()
                    "Events" -> fetchEvents()
                    "Budget Requests" -> fetchBudgetRequests()
                    "Room Bookings" -> fetchRoomBookings()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Initially fetch the first category data (Users)
        // fetchUsers()
    }


    private fun fetchUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                addSectionTitle("Users")
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    addUserView(user, document.id)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching users: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchEvents() {
        db.collection("events")
            .get()
            .addOnSuccessListener { documents ->
                addSectionTitle("Events")
                for (document in documents) {
                    val event = document.toObject(Event::class.java)
                    addEventView(event, document.id)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching events: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchBudgetRequests() {
        db.collection("budget_requests")
            .get()
            .addOnSuccessListener { documents ->
                addSectionTitle("Budget Requests")
                for (document in documents) {
                    val budgetRequest = document.toObject(BudgetRequest::class.java)
                    addBudgetRequestView(budgetRequest, document.id)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching budget requests: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchRoomBookings() {
        db.collection("roomlist")
            .get()
            .addOnSuccessListener { documents ->
                addSectionTitle("Room Bookings")
                for (document in documents) {
                    val roomId = document.id
                    val bookings = document.get("bookings") as? Map<String, Map<String, Any>> // Get all bookings for this room
                    if (bookings != null) {
                        addRoomBookingView(roomId, bookings)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching room bookings: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addRoomBookingView(roomId: String, bookings: Map<String, Map<String, Any>>) {
        val roomTextView = TextView(this).apply {
            text = "Room ID: $roomId"
            textSize = 18f
            setPadding(16, 16, 16, 8)

            // Set layout parameters with top and bottom margins for roomTextView
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 8) // Adjust margins as needed
            }
        }

        linearLayoutContainer.addView(roomTextView)

        for ((bookingId, bookingData) in bookings) {
            val bookingTextView = TextView(this).apply {
                text = """
                Booking ID: $bookingId
                Club: ${bookingData["club_name"]}
                Date: ${bookingData["date"]}
                Start Time: ${bookingData["start_time"]}
                End Time: ${bookingData["end_time"]}
                Purpose: ${bookingData["purpose"]}
                Booked by: ${bookingData["username"]}
            """.trimIndent()
                setPadding(32, 8, 16, 8)
                setBackgroundResource(R.drawable.card_border)

                // Set layout parameters with top and bottom margins for bookingTextView
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8) // Adjust margins as needed
                }
            }


            linearLayoutContainer.addView(bookingTextView)
        }
    }



    private fun addSectionTitle(title: String) {
        val titleTextView = TextView(this).apply {
            text = title
            textSize = 20f
            setPadding(16, 16, 16, 16)
        }
        linearLayoutContainer.addView(titleTextView)
    }

    private fun addUserView(user: User , documentId: String) {
        val cardView = CardView(this).apply {
            radius = 12f
            setContentPadding(16, 16, 16, 16)
            cardElevation = 8f
            setBackgroundResource(R.drawable.card_border)

            // Set layout parameters with top and bottom margins
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16) // Only top and bottom margins
            }
        }

        val userTextView = TextView(this).apply {
            text = "Username: ${user.username}\nPhone: ${user.phoneNumber}\nDepartment: ${user.department}\nClub Role: ${user.club_role}"
            setPadding(8, 8, 8, 8)
            textSize = 16f
        }

        val editUserButton = Button(this).apply {
            text = "Edit User"
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                // Show the edit dialog for User
                showEditDialog(dataType = "User", documentId = documentId, dataObject = user)
            }
        }

        cardView.addView(userTextView)

        cardView.setOnClickListener {
            showUserDetailsDialog(user)
        }
        linearLayoutContainer.addView(cardView)
        linearLayoutContainer.addView(editUserButton)
    }

    private fun addEventView(event: Event, documentId: String) {
        val cardView = CardView(this).apply {
            radius = 12f
            setContentPadding(16, 16, 16, 16)
            cardElevation = 8f
            setBackgroundResource(R.drawable.card_border)

            // Set layout parameters with top and bottom margins
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16)
            }
        }

        val eventTextView = TextView(this).apply {
            text = "Event Name: ${event.eventName}\nStart Date: ${event.eventStartDate}\nStatus: ${event.status}"
            setPadding(8, 8, 8, 8)
            textSize = 16f
        }

        val editUserButton = Button(this).apply {
            text = "Edit User"
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                // Show the edit dialog for User
                showEditDialog(dataType = "Event", documentId = documentId, dataObject = event)
            }
        }

        cardView.addView(eventTextView)
        cardView.setOnClickListener {
            showEventDetailsDialog(event)
        }
        linearLayoutContainer.addView(cardView)
        linearLayoutContainer.addView(editUserButton)
    }

    private fun addBudgetRequestView(budgetRequest: BudgetRequest, documentId: String) {
        val cardView = CardView(this).apply {
            radius = 12f
            setContentPadding(16, 16, 16, 16)
            cardElevation = 8f
            setBackgroundResource(R.drawable.card_border)

            // Set layout parameters with top and bottom margins
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16)
            }


        }

        val budgetTextView = TextView(this).apply {
            text = "Event Name: ${budgetRequest.eventName}\nTotal Budget: ${budgetRequest.value}\nApproved: ${budgetRequest.approved}"
            setPadding(8, 8, 8, 8)
            textSize = 16f
        }

        val editUserButton = Button(this).apply {
            text = "Edit User"
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                // Show the edit dialog for User
                showEditDialog(dataType = "Budget Request", documentId = documentId, dataObject = budgetRequest)
            }
        }

        cardView.addView(budgetTextView)
        cardView.setOnClickListener {
            showBudgetRequestDetailsDialog(budgetRequest)
        }

        linearLayoutContainer.addView(cardView)

        linearLayoutContainer.addView(editUserButton)
    }

    private fun showUserDetailsDialog(user: User) {
        AlertDialog.Builder(this)
            .setTitle("User Details")
            .setMessage("""
            Username: ${user.username}
            Phone: ${user.phoneNumber}
            Department: ${user.department}
            Club Role: ${user.club_role}
            Semester: ${user.semester}
            Club: ${user.club}
            Club Level: ${user.club_clr_level}
        """.trimIndent())
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showEventDetailsDialog(event: Event) {
        AlertDialog.Builder(this)
            .setTitle("Event Details")
            .setMessage("""
            Event Name: ${event.eventName}
            Description: ${event.eventDescription}
            Start Date: ${event.eventStartDate}
            End Date: ${event.eventEndDate}
            Features: ${event.eventFeatures.joinToString(", ")}
            Status: ${event.status}
            Signed By: ${event.signedBy}
            Club: ${event.club}
            Added By: ${event.addedBy}
        """.trimIndent())
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showBudgetRequestDetailsDialog(budgetRequest: BudgetRequest) {
        AlertDialog.Builder(this)
            .setTitle("Budget Request Details")
            .setMessage("""
            User ID: ${budgetRequest.userId}
            Event Name: ${budgetRequest.eventName}
            Total Budget: ${budgetRequest.value}
            Reason: ${budgetRequest.reason}
            Description: ${budgetRequest.description}
            Spend Amount: ${budgetRequest.spendAmount}
            Approved: ${budgetRequest.approved}
        """.trimIndent())
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showEditDialog(dataType: String, documentId: String, dataObject: Any) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_data, null)

        // Configure the view based on dataType
        when (dataType) {
            "User" -> {
                dialogView.findViewById<EditText>(R.id.usernameField).visibility = View.VISIBLE
                dialogView.findViewById<EditText>(R.id.phoneField).visibility = View.VISIBLE
                dialogView.findViewById<EditText>(R.id.departmentField).visibility = View.VISIBLE
            }
            "Event" -> {
                dialogView.findViewById<EditText>(R.id.eventNameField).visibility = View.VISIBLE
                dialogView.findViewById<EditText>(R.id.eventDescriptionField).visibility = View.VISIBLE
            }
            "Budget Request" -> {
                dialogView.findViewById<EditText>(R.id.budgetValueField).visibility = View.VISIBLE
                dialogView.findViewById<EditText>(R.id.budgetReasonField).visibility = View.VISIBLE
            }
            "Room Booking" -> {
                dialogView.findViewById<EditText>(R.id.roomNameField).visibility = View.VISIBLE
            }
        }

        val builder = AlertDialog.Builder(this).apply {
            setTitle("Edit $dataType")
            setView(dialogView)
            setPositiveButton("Save") { _, _ ->
                val newValues = getUpdatedValues(dialogView, dataType)
                updateFirestoreData(dataType, documentId, newValues)
            }
            setNegativeButton("Cancel", null)
        }
        builder.create().show()
    }

    // Get new values from dialog fields
    private fun getUpdatedValues(view: View, dataType: String): Map<String, Any> {
        val values = mutableMapOf<String, Any>()

        when (dataType) {
            "User" -> {
                values["username"] = view.findViewById<EditText>(R.id.usernameField).text.toString()
                values["phoneNumber"] = view.findViewById<EditText>(R.id.phoneField).text.toString()
                values["department"] = view.findViewById<EditText>(R.id.departmentField).text.toString()
            }
            "Event" -> {
                values["name"] = view.findViewById<EditText>(R.id.eventNameField).text.toString()
                values["description"] = view.findViewById<EditText>(R.id.eventDescriptionField).text.toString()
            }
            "Budget Request" -> {
                values["value"] = view.findViewById<EditText>(R.id.budgetValueField).text.toString()
                values["reason"] = view.findViewById<EditText>(R.id.budgetReasonField).text.toString()
            }
            "Room Booking" -> {
                values["roomName"] = view.findViewById<EditText>(R.id.roomNameField).text.toString()
            }
        }
        return values
    }

    // Update Firestore data based on collection
    private fun updateFirestoreData(dataType: String, documentId: String, updatedValues: Map<String, Any>) {
        val collectionName = when (dataType) {
            "User" -> "users"
            "Event" -> "events"
            "Budget Request" -> "budget_requests"
            "Room Booking" -> "roomlist"
            else -> ""
        }

        if (collectionName.isNotEmpty()) {
            db.collection(collectionName).document(documentId).update(updatedValues)
                .addOnSuccessListener {
                    Toast.makeText(this, "$dataType updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update $dataType: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }



}
