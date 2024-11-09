package com.example.bossapp

import Message
import MessageAdapter
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommunicationActivity : AppCompatActivity() {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: ImageButton
    private val messages = mutableListOf<Message>()
    private lateinit var messageAdapter: MessageAdapter
    private var currentUsername: String = "Anonymous"
    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.communication)

        // Initialize views
        messageRecyclerView = findViewById(R.id.messageRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        messageAdapter = MessageAdapter(messages)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        // Fetch the current username from Firestore
        fetchCurrentUsername()

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                addMessage(currentUsername, messageText)
                messageInput.text.clear()
            }
        }
    }

    // Fetch the current user's username from Firestore
    private fun fetchCurrentUsername() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userId = user.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Fetch the username from Firestore
                        currentUsername = document.getString("username") ?: "Anonymous"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching username: ${e.message}", Toast.LENGTH_SHORT).show()
                    currentUsername = "Anonymous"
                }
        }
    }

    // Function to add a message to the list and update the RecyclerView
    private fun addMessage(username: String, text: String) {
        val message = Message(text, username)
        messages.add(message)
        messageAdapter.notifyItemInserted(messages.size - 1)
        messageRecyclerView.scrollToPosition(messages.size - 1)
    }
}
