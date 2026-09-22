package com.example.studylinksa


import android.util.Base64
import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordUtils {

    private const val ITERATIONS = 12000
    private const val KEY_LENGTH = 256

    fun hashPassword(password: String, salt: ByteArray): String {

        val spec = PBEKeySpec(
            password.toCharArray(),
            salt,
            ITERATIONS,
            KEY_LENGTH
        )

        val factory =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")

        val hash = factory.generateSecret(spec).encoded

        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun generateSalt(): ByteArray {
        val salt = ByteArray(16)
        java.security.SecureRandom().nextBytes(salt)
        return salt
    }
}