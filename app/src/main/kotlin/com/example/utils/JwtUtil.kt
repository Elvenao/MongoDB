package com.example.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import com.example.model.UserLoginDto
import java.util.*

object JwtUtil {
    private val secretKey = Keys.hmacShaKeyFor("8da949392%1!5423_381j39ja2$6asdfas12".toByteArray())

    /* 
    fun generateAccessToken(email: String): String {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 min
            .signWith(secretKey)
            .compact()
    }
            */
    fun generateAccessToken(email: String): String {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 10 minutos
            .signWith(secretKey)
            .compact()
    }

    fun generateRefreshToken(email: String): String {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 días
            .signWith(secretKey)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body

            val expiration = claims.expiration
            expiration.after(Date())
        } catch (e: Exception) {
            false // token inválido o expirado
        }
    }

    fun validateRefreshToken(token: String): String? {
        try {
            val claims = Jwts.parser()
                .setSigningKey(secretKey) // misma clave usada para firmar refreshToken
                .parseClaimsJws(token)
                .body

            return claims.subject // Suponiendo que guardaste el email como subject
        } catch (e: Exception) {
            return null // Token inválido o expirado
        }
    }

    fun getSubject(token: String): String? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body

            claims.subject
        } catch (e: Exception) {
            null
        }
    }
}
