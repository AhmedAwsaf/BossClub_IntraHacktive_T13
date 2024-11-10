package com.example.bossapp


import Message
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


        messageRecyclerView = findViewById(R.id.messageRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)


        messageAdapter = MessageAdapter(messages)
        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter


        fetchCurrentUsername()


        loadMessages()


        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                messageInput.text.clear()
            }
        }
    }


    private fun fetchCurrentUsername() {
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        user?.let {
            db.collection("users").document(it.uid).get()
                .addOnSuccessListener { document ->
                    currentUsername = document.getString("username") ?: "Anonymous"
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error fetching username: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun sendMessage(text: String) {
        val message = Message(text, currentUsername, System.currentTimeMillis())
        db.collection("messages").add(message)
            .addOnSuccessListener {
                Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error sending message: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun loadMessages() {
        db.collection("messages")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(this, "Error loading messages: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    messages.clear()
                    for (document in snapshot.documents) {
                        val text = document.getString("text") ?: ""
                        val username = document.getString("username") ?: "Anonymous"
                        val timestamp = document.getLong("timestamp") ?: 0L


                        if (timestamp != 0L) {
                            val message = Message(text, username, timestamp)
                            messages.add(message)
                        }
                    }

                    messages.sortBy { it.timestamp }

                    messageAdapter.notifyDataSetChanged()
                    messageRecyclerView.scrollToPosition(messages.size - 1)
                }
            }
    }

}
