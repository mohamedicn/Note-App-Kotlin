package com.example.finial_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cat) // Ensure this matches your `cat.xml` file
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
        val buttonCreate = findViewById<Button>(R.id.imageButtoncreatee)
        val parentLayout = findViewById<LinearLayout>(R.id.parentLayout)

        // Load and display saved categories
        displaySavedCategories(parentLayout)

        // Navigate to CrActivity for adding new categories
        buttonCreate.setOnClickListener {
            val intent = Intent(this, CrActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val parentLayout = findViewById<LinearLayout>(R.id.parentLayout)
        parentLayout.removeAllViews() // Clear existing views

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val savedCategories = sharedPreferences.getStringSet("CATEGORY_NAMES", emptySet())

        if (!savedCategories.isNullOrEmpty()) {
            for (category in savedCategories) {
                Log.d("CatActivity", "Category retrieved: $category")
            }
        } else {
            Log.d("CatActivity", "No categories retrieved or set is empty.")
        }

        displaySavedCategories(parentLayout) // Reload and display saved categories
    }


    private fun displaySavedCategories(parentLayout: LinearLayout) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val savedCategories = sharedPreferences.getStringSet("CATEGORY_NAMES", emptySet()) ?: emptySet()

        if (savedCategories.isEmpty()) {
            // If no categories exist, show a message
            val emptyMessage = TextView(this).apply {
                text = "No categories found. Please add some."
                textSize = 16f
                setTextColor(resources.getColor(android.R.color.black, null))
                setPadding(16, 16, 16, 16)
            }
            parentLayout.addView(emptyMessage)
        } else {
            for (categoryName in savedCategories) {
                addCategoryCard(parentLayout, categoryName)
            }
        }
    }


    private fun addCategoryCard(parentLayout: LinearLayout, categoryName: String) {
        // Create a new LinearLayout dynamically
        val cardLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16) // Add space between cards
            }
            orientation = LinearLayout.VERTICAL
            setBackgroundResource(R.drawable.rounded_edittext) // Ensure this drawable exists
            setPadding(16, 16, 16, 16)
        }

        // Create a TextView for the category name
        val categoryTextView = TextView(this).apply {
            text = categoryName
            textSize = 20f
            setTextColor(resources.getColor(android.R.color.black, null))
        }

        // Add the TextView to the card layout
        cardLayout.addView(categoryTextView)

        // Add the card layout to the parent layout
        parentLayout.addView(cardLayout)
    }

}
