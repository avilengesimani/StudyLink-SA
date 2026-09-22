package com.example.studylinksa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var registerButton: Button

    private lateinit var userStorage: UserStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        userStorage = UserStorage(this)

        name = findViewById(R.id.etName)
        email = findViewById(R.id.etRegisterEmail)
        password = findViewById(R.id.etRegisterPassword)
        confirmPassword =
            findViewById(R.id.etConfirmPassword)

        registerButton =
            findViewById(R.id.btnCreateAccount)

        registerButton.setOnClickListener {

            val nameText = name.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString()
            val confirmText =
                confirmPassword.text.toString()

            if (
                nameText.isEmpty() ||
                emailText.isEmpty() ||
                passwordText.isEmpty()
            ) {

                Toast.makeText(
                    this,
                    "Please complete all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (passwordText.length < 6) {

                Toast.makeText(
                    this,
                    "Password must contain at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (passwordText != confirmText) {

                Toast.makeText(
                    this,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val created =
                userStorage.registerUser(
                    nameText,
                    emailText,
                    passwordText
                )

            if (created) {

                Toast.makeText(
                    this,
                    "Account created successfully",
                    Toast.LENGTH_SHORT
                ).show()

                startActivity(
                    Intent(
                        this,
                        MainActivity::class.java
                    )
                )

                finish()

            } else {

                Toast.makeText(
                    this,
                    "An account with this email already exists",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}