package com.example.controller

import com.example.model.Usuario
import com.example.repository.UsuarioRepository
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus

@RestController
@RequestMapping("/api")
class RegisterController(private val usuarioRepository: UsuarioRepository) {

    @PostMapping("/registro")
    fun registrarUsuario(@RequestBody usuario: Usuario): ResponseEntity<Any> {
        // Puedes validar si ya existe
        if (usuarioRepository.existsByEmail(usuario.email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(mapOf("success" to false, "message" to "El correo ya está registrado"))
        }

        // Guardar usuario en la base de datos
        usuarioRepository.save(usuario)

        return ResponseEntity.ok(mapOf("success" to true, "message" to "Usuario registrado con éxito"))
    }
}
