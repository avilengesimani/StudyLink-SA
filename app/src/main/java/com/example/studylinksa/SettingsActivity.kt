package com.example.studylinksa

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var language: Spinner
    private lateinit var notifications: Switch
    private lateinit var saveButton: Button

    private val languageTags = arrayOf("en", "xh", "zu")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        name = findViewById(R.id.etSettingName)
        language = findViewById(R.id.spLanguage)
        notifications = findViewById(R.id.switchNotifications)
        saveButton = findViewById(R.id.btnSaveSettings)

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.language_options)
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        language.adapter = adapter

        val preferences = getSharedPreferences(
            "StudyLinkSettings",
            MODE_PRIVATE
        )

        name.setText(preferences.getString("name", ""))

        notifications.isChecked =
            preferences.getBoolean("notifications", true)

        // Get the currently active application language.
        val currentTag =
            AppCompatDelegate.getApplicationLocales()
                .get(0)?.language ?: "en"

        val selectedIndex = languageTags.indexOf(currentTag)
            .let { if (it >= 0) it else 0 }

        language.setSelection(selectedIndex)

        saveButton.setOnClickListener {

            val selectedIndex = language.selectedItemPosition
            val selectedLanguage = languageTags[selectedIndex]

            // Save the existing user settings.
            preferences.edit()
                .putString("name", name.text.toString())
                .putString(
                    "language",
                    resources.getStringArray(R.array.language_options)[selectedIndex]
                )
                .putBoolean(
                    "notifications",
                    notifications.isChecked
                )
                .apply()

            val activeLanguage =
                AppCompatDelegate.getApplicationLocales()
                    .get(0)?.language ?: ""

            if (activeLanguage != selectedLanguage) {

                AppCompatDelegate.setApplicationLocales(
                    LocaleListCompat.forLanguageTags(selectedLanguage)
                )

            } else {

                Toast.makeText(
                    this,
                    getString(R.string.settings_saved),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}