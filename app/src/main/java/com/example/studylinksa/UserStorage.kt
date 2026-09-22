package com.example.studylinksa


import android.content.Context

class UserStorage(context: Context) {

    private val preferences =
        context.getSharedPreferences(
            "StudyLinkUsers",
            Context.MODE_PRIVATE
        )

    fun registerUser(
        name: String,
        email: String,
        password: String
    ): Boolean {

        val cleanEmail = email.trim().lowercase()

        if (preferences.contains("user_$cleanEmail")) {
            return false
        }

        val salt = PasswordUtils.generateSalt()

        val passwordHash =
            PasswordUtils.hashPassword(password, salt)

        preferences.edit()
            .putString("user_$cleanEmail", name)
            .putString(
                "password_$cleanEmail",
                passwordHash
            )
            .putString(
                "salt_$cleanEmail",
                android.util.Base64.encodeToString(
                    salt,
                    android.util.Base64.NO_WRAP
                )
            )
            .apply()

        return true
    }

    fun loginUser(
        email: String,
        password: String
    ): Boolean {

        val cleanEmail = email.trim().lowercase()

        val storedHash =
            preferences.getString(
                "password_$cleanEmail",
                null
            ) ?: return false

        val saltString =
            preferences.getString(
                "salt_$cleanEmail",
                null
            ) ?: return false

        val salt =
            android.util.Base64.decode(
                saltString,
                android.util.Base64.NO_WRAP
            )

        val enteredHash =
            PasswordUtils.hashPassword(
                password,
                salt
            )

        return storedHash == enteredHash
    }

    fun getName(email: String): String {

        return preferences.getString(
            "user_${email.trim().lowercase()}",
            "Student"
        ) ?: "Student"
    }
}