package com.example.controller

import com.example.repository.UsuarioLoginRepository
import com.example.model.UserLoginDto
import com.example.model.LoginResponse
import com.example.model.RefreshTokenRequest
import com.example.utils.JwtUtil
import com.example.model.TokenResponse

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


import org.springframework.security.crypto.bcrypt.BCrypt // para validar el hash

@RestController
@RequestMapping("/api/auth")
class LoginController(
    val usuarioRepository: UsuarioLoginRepository
) {
    @PostMapping("/login")
    fun login(@RequestBody userLoginDto: UserLoginDto): ResponseEntity<Any> {
        val emailDescifrado = userLoginDto.email // si lo tienes cifrado
        var usuario = usuarioRepository.findByUserName(emailDescifrado)
        
        if(usuario != null){
            val passwordValida = BCrypt.checkpw(userLoginDto.password, usuario.password) 
            return if (passwordValida) {
            // Aquí puedes generar y devolver un token, o lo que uses para sesiones
            val accessToken = JwtUtil.generateAccessToken(usuario)
            val refreshToken = JwtUtil.generateRefreshToken(usuario)
            ResponseEntity.ok(LoginResponse(
                true,
                "Inicio de sesión exitoso",
                accessToken,
                refreshToken,
                //usuario.id,
                //usuario.userName
                ))
            } else {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LoginResponse(false,"Contraseña incorrecta"))
            }
        }
        usuario = usuarioRepository.findByEmail(emailDescifrado)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado")

        val passwordValida = BCrypt.checkpw(userLoginDto.password, usuario.password)

        return if (passwordValida) {
            // Aquí puedes generar y devolver un token, o lo que uses para sesiones
            val accessToken = JwtUtil.generateAccessToken(usuario)
            val refreshToken = JwtUtil.generateRefreshToken(usuario)
            ResponseEntity.ok(LoginResponse(
                true,
                "Inicio de sesión exitoso",
                accessToken,
                refreshToken,
                //usuario.id,
                //usuario.userName
                ))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LoginResponse(false,"Contraseña incorrecta"))
        }
    }

    @PostMapping("/refresh")
    fun refreshAccessToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<Any> {
        val refreshToken = request.refreshToken

        val userEmail = JwtUtil.validateRefreshToken(refreshToken)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido o expirado")

        val user = usuarioRepository.findByEmail(userEmail)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado")

        val newAccessToken = JwtUtil.generateAccessToken(user)
        val newRefreshToken = JwtUtil.generateRefreshToken(user) // opcional

        return ResponseEntity.ok(
            TokenResponse(
                accessToken = newAccessToken,
                refreshToken = newRefreshToken
            )
        )
    }
}
