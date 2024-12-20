package com.example.finial_project

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu

class MainActivity : AppCompatActivity() {

    private var selectedCategory: String? = null  // Store the selected category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Load notes when MainActivity is created
        loadNotes()
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
        // Handling popup menu for category selection
        val chooseCategoryButton = findViewById<Button>(R.id.imageButtonchoose)
        chooseCategoryButton.setOnClickListener { view ->
            val popupMenu = PopupMenu(this, view)
            val menu = popupMenu.menu
            loadCategories(menu)  // Dynamically load categories

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.hideAll -> {
                        selectedCategory = null  // Hide all, set to null
                        chooseCategoryButton.text = "Show All"  // Update button text to "Show All"
                    }
                    else -> {
                        selectedCategory = item.title.toString()  // Update selected category
                        chooseCategoryButton.text = item.title.toString()  // Update button text
                    }
                }
                loadNotes()  // Reload notes to filter based on selected category
                true
            }
            popupMenu.show()
        }

        // Handling button clicks for navigating to other activities
        findViewById<Button>(R.id.imageButtoncat).setOnClickListener {
            startActivity(Intent(this, CatActivity::class.java))
        }

        findViewById<Button>(R.id.imageButtoncreate).setOnClickListener {
            startActivityForResult(Intent(this, NoteActivity::class.java), 1)
        }
    }

    // Load categories dynamically from SharedPreferences
    private fun loadCategories(menu: android.view.Menu) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val savedCategories = sharedPreferences.getStringSet("CATEGORY_NAMES", emptySet()) ?: emptySet()

        // Add categories to the menu dynamically
        savedCategories.forEach { category ->
            menu.add(Menu.NONE, category.hashCode(), Menu.NONE, category)
        }
        // Add "Hide All" option
        menu.add(Menu.NONE, R.id.hideAll, Menu.NONE, "Show All")
    }

    // Save note data to SharedPreferences
    private fun saveNoteToPreferences(noteData: String) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Fetch existing notes and add the new one
        val existingNotes = sharedPreferences.getStringSet("NOTES", setOf())?.toMutableSet() ?: mutableSetOf()
        existingNotes.add(noteData)

        // Save the updated notes back to preferences
        editor.putStringSet("NOTES", existingNotes)
        editor.apply()
    }

    // Load and display notes dynamically with category filter
    private fun loadNotes() {
        val notesContainer = findViewById<LinearLayout>(R.id.notesContainer)
        notesContainer.removeAllViews()  // Clear any existing views

        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val savedNotes = sharedPreferences.getStringSet("NOTES", setOf())?.toList() ?: emptyList()

        // Filter notes based on the selected category
        val filteredNotes = if (selectedCategory.isNullOrEmpty()) {
            savedNotes  // Show all notes if no category is selected
        } else {
            savedNotes.filter { note ->
                val noteParts = note.split(", ").map { it.substringAfter(": ") }
                val noteCategory = noteParts[0]  // Assuming category is the first part
                noteCategory == selectedCategory
            }
        }

        filteredNotes.forEach { noteData ->
            val noteView = createNoteView(noteData)
            notesContainer.addView(noteView)
        }
    }

    // Create a view for displaying a note
    private fun createNoteView(noteData: String): LinearLayout {
        val noteParts = noteData.split(", ").map { it.substringAfter(": ") }
        val (noteCategory, noteDate, noteDescription, noteTitle) = noteParts

        // Create the LinearLayout for the note view
        val noteView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundResource(R.drawable.rounded_edittext)
        }

        // Create and add the date TextView
        val dateView = TextView(this).apply {
            text = noteDate
            textSize = 12f
            setTextColor(resources.getColor(R.color.gray))
        }
        noteView.addView(dateView)

        // Create and add the title TextView
        val titleView = TextView(this).apply {
            text = noteDescription
            textSize = 20f
            setTextColor(resources.getColor(R.color.black))
            setPadding(0, 8, 0, 0)
        }
        noteView.addView(titleView)

        // Create and add the description TextView
        val descriptionView = TextView(this).apply {
            text = noteTitle
            textSize = 14f
            setTextColor(resources.getColor(R.color.dark_gray))
            setPadding(0, 8, 0, 0)
        }
        noteView.addView(descriptionView)

        // Set the LayoutParams with bottom margin
        val layoutParams = noteView.layoutParams ?: LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Set margins (left, top, right, bottom)
        (layoutParams as LinearLayout.LayoutParams).setMargins(16, 16, 16, 10)  // 10 for the bottom margin
        noteView.layoutParams = layoutParams

        // Set the click listener to navigate to TextActivity
        noteView.setOnClickListener {
            val intent = Intent(this, TextActivity::class.java).apply {
                // Pass the details of the note to the TextActivity
                putExtra("NOTE_CATEGORY", noteCategory)
                putExtra("NOTE_DATE", noteDate)
                putExtra("NOTE_DESCRIPTION", noteDescription)
                putExtra("NOTE_TITLE", noteTitle)
            }
            startActivity(intent)
        }

        return noteView
    }

    // Function to show a toast message
    private fun showToast(message: String): Boolean {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        return true
    }

    // Handle result from NoteActivity to refresh notes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Retrieve the new note from the intent
            val newNote = data?.getStringExtra("NEW_NOTE")
            newNote?.let {
                onNoteSaved(it)  // Save and reload notes
            }
        }
    }

    // Call this function when a note is saved
    fun onNoteSaved(noteData: String) {
        saveNoteToPreferences(noteData)
        loadNotes()  // Re-load the notes after saving
    }
}
