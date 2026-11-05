package com.example.studylinksa

import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.contentOrNull

data class AcademicField(
    val key: String,
    val label: String,
    val required: Boolean = false,
    val hint: String = label
)

abstract class AcademicScreenActivity : AppCompatActivity() {

    abstract val tableName: String
    abstract val screenTitle: String
    abstract val fields: List<AcademicField>

    open val defaultValues: Map<String, String> = emptyMap()
    open val supportsCompletion: Boolean = false

    private lateinit var recordContainer: LinearLayout
    private lateinit var statusText: TextView

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }

        scroll.addView(layout)

        val heading = TextView(this).apply {
            text = screenTitle
            textSize = 27f
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        statusText = TextView(this).apply {
            text = "Loading records..."
            textSize = 14f
            setPadding(0, dp(12), 0, dp(12))
        }

        val addButton = Button(this).apply {
            text = "Add New Record"
            setOnClickListener { showRecordDialog(null) }
        }

        val refreshButton = Button(this).apply {
            text = "Refresh"
            setOnClickListener { loadRecords() }
        }

        recordContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val backButton = Button(this).apply {
            text = "Back to Dashboard"
            setOnClickListener { finish() }
        }

        layout.addView(heading)
        layout.addView(statusText)
        layout.addView(addButton)
        layout.addView(refreshButton)
        layout.addView(recordContainer)
        layout.addView(backButton)

        setContentView(scroll)

        loadRecords()
    }

    private fun loadRecords() {
        statusText.text = "Loading records..."
        recordContainer.removeAllViews()

        lifecycleScope.launch {
            try {
                val records = AcademicRepository.getRecords(tableName)

                recordContainer.removeAllViews()

                if (records.isEmpty()) {
                    statusText.text = "No records found. Add your first record!"
                } else {
                    statusText.text = "Records found: ${records.size}"

                    records.forEach { record ->
                        displayRecord(record)
                    }
                }

            } catch (e: Exception) {
                statusText.text = "Unable to load records: ${e.message}"
            }
        }
    }

    private fun displayRecord(record: JsonObject) {
        val recordId = record["id"]
            ?.jsonPrimitive
            ?.contentOrNull ?: return

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(12), dp(14), dp(12))
            setBackgroundColor(0xFFF0F0F0.toInt())
        }

        fields.forEach { field ->
            val value = record[field.key]
                ?.jsonPrimitive
                ?.contentOrNull ?: ""

            val line = TextView(this).apply {
                text = "${field.label}: ${if (value.isBlank()) "Not provided" else value}"
                textSize = 16f
                setPadding(0, dp(4), 0, dp(4))
            }

            card.addView(line)
        }

        val buttonRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val editButton = Button(this).apply {
            text = "Edit"
            setOnClickListener { showRecordDialog(record) }
        }

        val deleteButton = Button(this).apply {
            text = "Delete"
            setOnClickListener { confirmDelete(recordId) }
        }

        buttonRow.addView(
            editButton,
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )

        buttonRow.addView(
            deleteButton,
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )

        card.addView(buttonRow)

        if (supportsCompletion) {
            val completed = record["completed"]
                ?.jsonPrimitive
                ?.contentOrNull == "true"

            val completionButton = Button(this).apply {
                text = if (completed) "Mark as Incomplete" else "Mark as Completed"

                setOnClickListener {
                    changeCompletion(recordId, !completed)
                }
            }

            card.addView(completionButton)
        }

        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        params.setMargins(0, dp(10), 0, dp(10))
        recordContainer.addView(card, params)
    }

    private fun showRecordDialog(record: JsonObject?) {
        val editing = record != null
        val inputFields = mutableMapOf<String, EditText>()

        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(10), dp(20), 0)
        }

        fields.forEach { field ->
            val label = TextView(this).apply {
                text = field.label + if (field.required) " *" else ""
                textSize = 14f
            }

            val oldValue = record?.get(field.key)
                ?.jsonPrimitive
                ?.contentOrNull
                ?: defaultValues[field.key]
                ?: ""

            val input = EditText(this).apply {
                hint = field.hint
                setText(oldValue)
                textSize = 16f
            }

            inputFields[field.key] = input

            form.addView(label)
            form.addView(input)
        }

        val dialogScroll = ScrollView(this)
        dialogScroll.addView(form)

        val dialog = AlertDialog.Builder(this)
            .setTitle(if (editing) "Edit $screenTitle" else "Add $screenTitle")
            .setView(dialogScroll)
            .setNegativeButton("Cancel", null)
            .setPositiveButton(if (editing) "Update" else "Save", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val values = mutableMapOf<String, JsonPrimitive>()

                for (field in fields) {
                    val value = inputFields[field.key]?.text
                        ?.toString()?.trim().orEmpty()

                    if (field.required && value.isBlank()) {
                        inputFields[field.key]?.error = "This field is required"
                        return@setOnClickListener
                    }

                    if (value.isNotBlank()) {
                        values[field.key] = JsonPrimitive(value)
                    }
                }

                if (!editing) {
                    defaultValues.forEach { (key, value) ->
                        if (!values.containsKey(key)) {
                            values[key] = if (key == "completed") {
                                JsonPrimitive(value.toBoolean())
                            } else {
                                JsonPrimitive(value)
                            }
                        }
                    }
                }

                dialog.dismiss()

                lifecycleScope.launch {
                    try {
                        if (editing) {
                            AcademicRepository.updateRecord(
                                tableName,
                                record?.get("id")?.jsonPrimitive?.contentOrNull
                                    ?: throw Exception("Record ID missing"),
                                values
                            )
                        } else {
                            AcademicRepository.addRecord(tableName, values)
                        }

                        Toast.makeText(
                            this@AcademicScreenActivity,
                            "Record saved successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadRecords()

                    } catch (e: Exception) {
                        Toast.makeText(
                            this@AcademicScreenActivity,
                            "Save failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        dialog.show()
    }

    private fun confirmDelete(recordId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Record")
            .setMessage("Are you sure you want to delete this record?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    try {
                        AcademicRepository.deleteRecord(tableName, recordId)

                        Toast.makeText(
                            this@AcademicScreenActivity,
                            "Record deleted.",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadRecords()

                    } catch (e: Exception) {
                        Toast.makeText(
                            this@AcademicScreenActivity,
                            "Delete failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            .show()
    }

    private fun changeCompletion(recordId: String, completed: Boolean) {
        lifecycleScope.launch {
            try {
                AcademicRepository.updateRecord(
                    tableName,
                    recordId,
                    mapOf("completed" to JsonPrimitive(completed))
                )

                Toast.makeText(
                    this@AcademicScreenActivity,
                    "Assignment status updated.",
                    Toast.LENGTH_SHORT
                ).show()

                loadRecords()

            } catch (e: Exception) {
                Toast.makeText(
                    this@AcademicScreenActivity,
                    "Update failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}