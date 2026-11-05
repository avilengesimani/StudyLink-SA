package com.example.studylinksa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import io.github.jan.supabase.auth.auth

class RegisterActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        name = findViewById(R.id.etName)
        email = findViewById(R.id.etRegisterEmail)
        password = findViewById(R.id.etRegisterPassword)
        confirmPassword = findViewById(R.id.etConfirmPassword)
        registerButton = findViewById(R.id.btnCreateAccount)

        registerButton.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {

        val nameText = name.text.toString().trim()
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString()
        val confirmText = confirmPassword.text.toString()

        if (nameText.isEmpty() || emailText.isEmpty() ||
            passwordText.isEmpty() || confirmText.isEmpty()) {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (passwordText.length < 6) {
            Toast.makeText(this, "Password must contain at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        if (passwordText != confirmText) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        registerButton.isEnabled = false

        lifecycleScope.launch {
            try {
                SupabaseClientProvider.client.auth.signUpWith(Email) {
                    this.email = emailText
                    this.password = passwordText

                    this.data = buildJsonObject {
                        put("full_name", nameText)
                    }
                }

                Toast.makeText(
                    this@RegisterActivity,
                    "Account created! Please check your email and confirm your account before logging in.",
                    Toast.LENGTH_LONG
                ).show()

                startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                finish()

            } catch (e: Exception) {

                Toast.makeText(
                    this@RegisterActivity,
                    "Registration failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {
                registerButton.isEnabled = true
            }
        }
    }
}