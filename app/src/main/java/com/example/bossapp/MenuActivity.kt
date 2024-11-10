package com.example.bossapp

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MenuActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_menu)


        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()


        loadUserData()
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        Toast.makeText(this, "User data found ${document.data}", Toast.LENGTH_SHORT).show()
                        val username = document.getString("username") ?: ""
                        val club = document.getString("club") ?: ""
                        val department = document.getString("department") ?: ""


                        findViewById<TextView>(R.id.userNameText).text = username
                        findViewById<TextView>(R.id.clubNameText).text = "Club: $club"
                        findViewById<TextView>(R.id.club_deptText).text = "Department: $department"
                    } else {
                        Toast.makeText(this, "No user data found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
