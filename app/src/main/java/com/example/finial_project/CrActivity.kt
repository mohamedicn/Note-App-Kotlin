package com.example.finial_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CrActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catcreat) // Ensure this matches your `catcreat.xml` file
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
        val editText = findViewById<EditText>(R.id.CatNameInput)
        val createButton = findViewById<Button>(R.id.imageButtoncreateCat)

        createButton.setOnClickListener {
            val categoryName = editText.text.toString().trim()

            if (categoryName.isNotEmpty()) {
                // Save to SharedPreferences as part of a StringSet
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                // Retrieve the current set of categories or create a new one
                val savedCategories = sharedPreferences.getStringSet("CATEGORY_NAMES", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

                // Add the new category to the set
                savedCategories.add(categoryName)

                // Save the updated set back to SharedPreferences
                editor.putStringSet("CATEGORY_NAMES", savedCategories)
                editor.apply()

                // Show confirmation to the user
                Toast.makeText(this, "Category saved!", Toast.LENGTH_SHORT).show()

                // Clear the input field after saving
                editText.text.clear()
            } else {
                // Alert user to enter valid text
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
