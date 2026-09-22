package com.example.studylinksa

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var language: Spinner
    private lateinit var notifications: Switch
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        name = findViewById(R.id.etSettingName)
        language = findViewById(R.id.spLanguage)
        notifications =
            findViewById(R.id.switchNotifications)

        saveButton =
            findViewById(R.id.btnSaveSettings)

        val languages = arrayOf(
            "English",
            "isiXhosa",
            "isiZulu"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            languages
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        language.adapter = adapter

        val preferences =
            getSharedPreferences(
                "StudyLinkSettings",
                MODE_PRIVATE
            )

        name.setText(
            preferences.getString(
                "name",
                ""
            )
        )

        notifications.isChecked =
            preferences.getBoolean(
                "notifications",
                true
            )

        saveButton.setOnClickListener {

            preferences.edit()
                .putString(
                    "name",
                    name.text.toString()
                )
                .putString(
                    "language",
                    language.selectedItem.toString()
                )
                .putBoolean(
                    "notifications",
                    notifications.isChecked
                )
                .apply()

            Toast.makeText(
                this,
                "Settings saved",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}