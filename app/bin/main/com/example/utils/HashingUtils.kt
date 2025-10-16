package com.example.utils

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object HashingUtils {
    private val encoder = BCryptPasswordEncoder()
    fun hashingBCrypt(password: String): String { 
        val hashedPassword = encoder.encode(password)
        return hashedPassword
    }
}
