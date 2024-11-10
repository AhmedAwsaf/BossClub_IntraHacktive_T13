package com.example.bossapp

import android.os.Bundle
import android.text.InputType
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
import android.widget.CalendarView
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
                showEditDialog(dataType = "User", documentId = documentId, dataObject = user, name = user.username)
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
                showEditDialog(dataType = "Event", documentId = documentId, dataObject = event, name = event.eventName)
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
                showEditDialog(dataType = "Budget Request", documentId = documentId, dataObject = budgetRequest, name = budgetRequest.reason)
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

    private fun showEditDialog(dataType: String, documentId: String, dataObject: Any, name: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_data, null)
        val linearLayout = dialogView.findViewById<LinearLayout>(R.id.edit_linear_layout)
        val editTexts = mutableMapOf<String, EditText>()

        // Populate the fields based on data type
        when (dataType) {
            "User" -> {
                val user = dataObject as User
                //editTexts["username"] = createEditText("Username", user.username, linearLayout)
                //editTexts["phoneNumber"] = createEditText("Phone Number", user.phoneNumber, linearLayout)
                //editTexts["semester"] = createEditText("Semester", user.semester, linearLayout)
                //editTexts["department"] = createEditText("Department", user.department, linearLayout)
                editTexts["club"] = createEditText("Club", user.club, linearLayout)
                //editTexts["profilePictureUrl"] = createEditText("Profile Picture URL", user.profilePictureUrl, linearLayout)
                editTexts["user_type"] = createEditText("User Type", user.user_type, linearLayout)
                editTexts["club_role"] = createEditText("Club Role", user.club_role, linearLayout)
                editTexts["club_clr_level"] = createEditText("Club Color Level", user.club_clr_level.toString(), linearLayout)
                editTexts["club_dept"] = createEditText("Club Department", user.club_dept, linearLayout)
            }
            "Event" -> {
                val event = dataObject as Event
                //editTexts["eventName"] = createEditText("Event Name", event.eventName, linearLayout)
                editTexts["eventDescription"] = createEditText("Event Description", event.eventDescription, linearLayout)
                editTexts["eventStartDate"] = createEditText("Event Start Date", event.eventStartDate.toString(), linearLayout)
                editTexts["eventEndDate"] = createEditText("Event End Date", event.eventEndDate.toString(), linearLayout)
                //editTexts["club"] = createEditText("Club", event.club, linearLayout)
            }
            "Budget Request" -> {
                val budgetRequest = dataObject as BudgetRequest
                editTexts["value"] = createEditText("Budget Value", budgetRequest.value.toString(), linearLayout)
                //editTexts["reason"] = createEditText("Reason", budgetRequest.reason, linearLayout)
                editTexts["description"] = createEditText("Description", budgetRequest.description, linearLayout)
                editTexts["spendAmount"] = createEditText("Spend Amount", budgetRequest.spendAmount.toString(), linearLayout)
                editTexts["eventName"] = createEditText("Event Name", budgetRequest.eventName, linearLayout)
            }
            // Add other cases if needed
        }

        AlertDialog.Builder(this).apply {
            setTitle("Edit $dataType")
            setMessage(name)
            setView(dialogView)
            setPositiveButton("Save") { _, _ ->
                val newValues = getUpdatedValues(editTexts, dataType)
                updateFirestoreData(dataType, documentId, newValues)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    // Helper function to create EditText with pre-populated text
    private fun createEditText(hint: String, preFill: String, parent: LinearLayout): EditText {
        val editText = EditText(this)
        editText.hint = hint
        editText.setText(preFill)  // Set existing data here
        editText.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        parent.addView(editText)
        return editText
    }


    // Create a CalendarView for date selection
    private fun createCalendarView(label: String, parent: LinearLayout): CalendarView {
        val calendarView = CalendarView(this).apply {
            setOnDateChangeListener { _, year, month, day ->
                tag = "$year-${String.format("%02d", month + 1)}-${String.format("%02d", day)}"
            }
        }
        parent.addView(TextView(this).apply { text = label })
        parent.addView(calendarView)
        return calendarView
    }

    // Retrieve updated values from input fields
    private fun getUpdatedValues(editTexts: Map<String, View>, dataType: String): Map<String, Any> {
        val values = mutableMapOf<String, Any>()

        for ((key, view) in editTexts) {
            when (view) {
                is EditText -> {
                    // Check if the field is numeric to handle club_clr_level properly
                    values[key] = if (key == "club_clr_level") {
                        view.text.toString().toIntOrNull() ?: 0 // Use 0 or handle error as needed
                    } else {
                        view.text.toString()
                    }
                }
                is CalendarView -> values[key] = view.tag as? String ?: ""
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
