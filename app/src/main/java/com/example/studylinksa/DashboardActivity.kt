package com.example.studylinksa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        val welcome =
            findViewById<TextView>(R.id.tvWelcome)

        val points =
            findViewById<TextView>(R.id.tvPoints)

        val streak =
            findViewById<TextView>(R.id.tvStreak)

        val email =
            getSharedPreferences(
                "StudyLinkSession",
                MODE_PRIVATE
            ).getString("email", "") ?: ""

        val storage = UserStorage(this)

        welcome.text =
            "Welcome, ${storage.getName(email)}"

        points.text = "Points: 0"
        streak.text = "Study Streak: 0 days"

        findViewById<Button>(R.id.btnQuiz)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        QuizActivity::class.java
                    )
                )
            }

        findViewById<Button>(R.id.btnSettings)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        SettingsActivity::class.java
                    )
                )
            }

        findViewById<Button>(R.id.btnFlashcards)
            .setOnClickListener {

                startActivity(
                    Intent(
                        this,
                        FlashcardActivity::class.java
                    )
                )
            }

        findViewById<Button>(R.id.btnLogout)
            .setOnClickListener {

                getSharedPreferences(
                    "StudyLinkSession",
                    MODE_PRIVATE
                )
                    .edit()
                    .clear()
                    .apply()

                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )

                finish()
            }
    }
}