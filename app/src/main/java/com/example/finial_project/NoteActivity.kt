// NoteActivity
package com.example.finial_project
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import java.text.SimpleDateFormat
import java.util.*

class NoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.creatnote) // Ensure this matches your `creatnote.xml` file
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
        val chooseCategoryButton = findViewById<Button>(R.id.imageButtonchoose)
        val descriptionInput = findViewById<EditText>(R.id.disInput)
        val noteInput = findViewById<EditText>(R.id.textInput)
        val createNoteButton = findViewById<Button>(R.id.imageButtoncreate)

        var selectedCategory = ""

        // Handle category selection
        chooseCategoryButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)

            // Fetch categories from SharedPreferences
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val savedCategories = sharedPreferences.getStringSet("CATEGORY_NAMES", setOf()) ?: setOf()

            // Populate PopupMenu with categories
            if (savedCategories.isNotEmpty()) {
                for (categoryName in savedCategories) {
                    popupMenu.menu.add(categoryName) // Dynamically add items
                }
            } else {
                popupMenu.menu.add("No categories available")
            }

            // Handle menu item clicks
            popupMenu.setOnMenuItemClickListener { item ->
                selectedCategory = item.title.toString()
                chooseCategoryButton.text = selectedCategory
                Toast.makeText(this, "$selectedCategory selected", Toast.LENGTH_SHORT).show()
                true
            }

            popupMenu.show()
        }

        // Handle note creation
        createNoteButton.setOnClickListener {
            val description = descriptionInput.text.toString().trim()
            val noteContent = noteInput.text.toString().trim()

            if (selectedCategory.isNotEmpty() && description.isNotEmpty() && noteContent.isNotEmpty()) {
                // Save the note with metadata
                val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                val notesKey = "NOTES"
                val notes = sharedPreferences.getStringSet(notesKey, mutableSetOf())?.toMutableSet() ?: mutableSetOf()

                // Add note with metadata
                val date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                val noteData = "Category: $selectedCategory, Date: $date, Description: $description, Note: $noteContent"
                notes.add(noteData)

                // Save back to SharedPreferences
                editor.putStringSet(notesKey, notes)
                editor.apply()

                // Show confirmation and clear inputs
                Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
                descriptionInput.text.clear()
                noteInput.text.clear()
                chooseCategoryButton.text = "Choose category"
                selectedCategory = ""

                // Return to MainActivity with result to refresh notes
                val intent = Intent().apply {
                    putExtra("NEW_NOTE", noteData)
                }
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Please fill in all fields and choose a category or discrption and text \n", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
