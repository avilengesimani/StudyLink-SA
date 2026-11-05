package com.example.studylinksa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var googleLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPassword)

        loginButton = findViewById(R.id.btnLogin)
        registerButton = findViewById(R.id.btnRegister)
        googleLoginButton = findViewById(R.id.btnGoogleLogin)

        loginButton.setOnClickListener {
            loginUser()
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun loginUser() {

        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString()

        if (emailText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(
                this,
                "Please enter your email and password",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        loginButton.isEnabled = false

        lifecycleScope.launch {
            try {
                SupabaseClientProvider.client.auth.signInWith(Email) {
                    this.email = emailText
                    this.password = passwordText
                }

                getSharedPreferences("StudyLinkSession", MODE_PRIVATE)
                    .edit()
                    .putString("email", emailText)
                    .apply()

                Toast.makeText(
                    this@MainActivity,
                    "Login successful!",
                    Toast.LENGTH_SHORT
                ).show()

                openDashboard()

            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Login failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {
                loginButton.isEnabled = true
            }
        }
    }

    private fun signInWithGoogle() {

        val webClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID

        if (webClientId.isBlank()) {
            Toast.makeText(
                this,
                "Google Client ID is missing. Check local.properties.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        googleLoginButton.isEnabled = false

        lifecycleScope.launch {
            try {

                val googleOption =
                    GetSignInWithGoogleOption.Builder(webClientId)
                        .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleOption)
                    .build()

                val credentialManager = CredentialManager.create(this@MainActivity)

                val result = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = request
                )

                val credential = result.credential

                if (
                    credential !is CustomCredential ||
                    credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    throw Exception("Google did not return a valid ID token.")
                }

                val googleCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)

                SupabaseClientProvider.client.auth.signInWith(IDToken) {
                    idToken = googleCredential.idToken
                    provider = Google
                }

                getSharedPreferences("StudyLinkSession", MODE_PRIVATE)
                    .edit()
                    .putString("email", googleCredential.id)
                    .apply()

                Toast.makeText(
                    this@MainActivity,
                    "Google login successful!",
                    Toast.LENGTH_SHORT
                ).show()

                openDashboard()

            } catch (e: Exception) {

                Toast.makeText(
                    this@MainActivity,
                    "Google login failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()

            } finally {
                googleLoginButton.isEnabled = true
            }
        }
    }

    private fun openDashboard() {
        startActivity(
            Intent(this, DashboardActivity::class.java)
        )
        finish()
    }
}