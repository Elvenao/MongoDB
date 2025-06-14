package com.example.controller

import com.example.repository.UsuarioLoginRepository
import com.example.model.UserLoginDto
import com.example.model.LoginResponse

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
        val usuario = usuarioRepository.findByEmail(emailDescifrado)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado")

        val passwordValida = BCrypt.checkpw(userLoginDto.password, usuario.password)

        return if (passwordValida) {
            // Aquí puedes generar y devolver un token, o lo que uses para sesiones
            ResponseEntity.ok(LoginResponse(true,"Inicio de sesión exitoso"))
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(LoginResponse(false,"Contraseña incorrecta"))
        }
    }
}
