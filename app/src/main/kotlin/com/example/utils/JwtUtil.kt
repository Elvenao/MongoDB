package com.example.utils

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import com.example.model.UserLoginDto
import java.util.*
import com.example.model.Usuario
import java.net.InetAddress


object JwtUtil {
    
    private val ip = InetAddress.getLocalHost().hostAddress
    

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
    fun generateAccessToken(user: Usuario ): String {
        println("IP local del servidor: $ip")
        return Jwts.builder()
            .setSubject(user.email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() +  10 * 60 * 1000 )) // 10 minutos
            .claim("email", user.email)
            .claim("id",user.id)
            .claim("userName",user.userName)
            .claim("name", user.name)
            .claim("birthDate", user.birthDate)
            .claim("joiningDate", user.joiningDate)
            .claim("biography",user.biography)
            .claim("genres",user.genres)
            .claim("avatar", user.avatar)
            .claim("ip",ip)
            .claim("following",user.following)
            .claim("followers",user.followers)
            .signWith(secretKey)
            .compact()
    }

    fun generateRefreshToken(user:Usuario): String {
        println("Avatar del usuario: ${user.avatar}")
        return Jwts.builder()
            .setSubject(user.email)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 días
            .claim("email", user.email)
            .claim("id",user.id)
            .claim("userName",user.userName)
            .claim("name", user.name)
            .claim("birthDate", user.birthDate)
            .claim("joiningDate", user.joiningDate)
            .claim("biography",user.biography)
            .claim("genres",user.genres)
            .claim("avatar", user.avatar)
            .claim("following",user.following)
            .claim("followers",user.followers)
            .claim("ip",ip)
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
            println("AccessToken valido")
            val expiration = claims.expiration
            expiration.after(Date())
            
        } catch (e: ExpiredJwtException) {
        // Token expirado
            println("Token expirado")
            false
        } catch (e: JwtException) {
            // Token inválido en estructura o firma
            println("Token inválido: ${e.message}")
            false
        }
    }

    fun validateRefreshToken(token: String): String? {
        try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey) // misma clave usada para firmar refreshToken
                .build()
                .parseClaimsJws(token)
                .body
             println("Enviando refreshToken")
            return claims.subject // Suponiendo que guardaste el email como subject
           
        } catch (e: ExpiredJwtException) {
            println("Refresh token expirado")
            return null
        } catch (e: JwtException) {
            println("Refresh token inválido")
            return null
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
