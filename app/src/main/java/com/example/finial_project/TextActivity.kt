package com.example.finial_project

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import android.widget.Button
class TextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.showtext) // Set the layout for the TextActivity
        val homeButton = findViewById<Button>(R.id.imageButtonhome)

        // Set a click listener to navigate back to MainActivity
        homeButton.setOnClickListener {
            // Create an intent to go to MainActivity
            val intent = Intent(this, MainActivity::class.java)

            // Optional: You can clear the activity stack to ensure the back button does not return to this activity
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

            // Start the MainActivity
            startActivity(intent)

            // Optionally finish the current activity so it is removed from the back stack
            finish()
        }
        // Get the note data passed from MainActivity
        val noteCategory = intent.getStringExtra("NOTE_CATEGORY") ?: "Unknown Category"
        val noteDate = intent.getStringExtra("NOTE_DATE") ?: "No Date"
        val noteDescription = intent.getStringExtra("NOTE_DESCRIPTION") ?: "No Description"
        val noteTitle = intent.getStringExtra("NOTE_TITLE") ?: "No Title"

        // Find TextViews to display the note details
        val dateTextView = findViewById<TextView>(R.id.dateTextView)
        val descriptionTextView = findViewById<TextView>(R.id.descriptionTextView)
        val titleTextView = findViewById<TextView>(R.id.titleTextView)

        // Set the note details in the TextViews
        dateTextView.text = "Date: $noteDate"
        descriptionTextView.text = "Description: $noteDescription"
        titleTextView.text = "Title: $noteTitle"
    }
}
