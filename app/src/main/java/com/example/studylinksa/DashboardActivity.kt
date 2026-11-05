package com.example.studylinksa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)
        requestNotificationPermission()

        findViewById<Button>(R.id.btnTimetable).setOnClickListener {
            startActivity(Intent(this, TimetableActivity::class.java))
        }

        val welcome = findViewById<TextView>(R.id.tvWelcome)
        val points = findViewById<TextView>(R.id.tvPoints)
        val streak = findViewById<TextView>(R.id.tvStreak)

        points.text = "Points: 0"
        streak.text = "Study Streak: 0 days"

        loadUserDetails(welcome)

        findViewById<Button>(R.id.btnTimetable).setOnClickListener {
            startActivity(Intent(this, TimetableActivity::class.java))
        }

        findViewById<Button>(R.id.btnAssignments).setOnClickListener {
            startActivity(Intent(this, AssignmentsActivity::class.java))
        }

        findViewById<Button>(R.id.btnExams).setOnClickListener {
            startActivity(Intent(this, ExamsActivity::class.java))
        }

        findViewById<Button>(R.id.btnFlashcards).setOnClickListener {
            startActivity(Intent(this, FlashcardActivity::class.java))
        }

        findViewById<Button>(R.id.btnQuiz).setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        findViewById<Button>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            logoutUser()
        }
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                NotificationHelper.createChannel(this)
            }
        }

    private fun requestNotificationPermission() {

        NotificationHelper.createChannel(this)

        if (Build.VERSION.SDK_INT >= 33) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun loadUserDetails(welcome: TextView) {

        lifecycleScope.launch {
            try {
                val user = SupabaseClientProvider.client
                    .auth.currentUserOrNull()

                if (user == null) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Please log in again",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(
                        Intent(this@DashboardActivity, MainActivity::class.java)
                    )
                    finish()
                    return@launch
                }

                val fullName = user.userMetadata
                    ?.get("full_name")
                    ?.jsonPrimitive
                    ?.contentOrNull

                welcome.text = if (!fullName.isNullOrBlank()) {
                    "Welcome, $fullName"
                } else {
                    "Welcome, ${user.email ?: "Student"}"
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@DashboardActivity,
                    "Could not load user details: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun logoutUser() {

        lifecycleScope.launch {
            try {
                SupabaseClientProvider.client.auth.signOut()

                getSharedPreferences(
                    "StudyLinkSession",
                    MODE_PRIVATE
                ).edit().clear().apply()

                Toast.makeText(
                    this@DashboardActivity,
                    "Logged out successfully",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(this@DashboardActivity, MainActivity::class.java)
                )

                finish()

            } catch (e: Exception) {
                Toast.makeText(
                    this@DashboardActivity,
                    "Logout failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}