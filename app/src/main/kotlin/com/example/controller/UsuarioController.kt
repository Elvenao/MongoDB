package com.example.controller

import com.example.model.Usuario
import com.example.service.UsuarioService



import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api")
class UsersController(
    private val usuarioService: UsuarioService
) {

    @GetMapping("/users")
    fun listAll(): ResponseEntity<List<Usuario>> {
        val usuarios: List<Usuario> = usuarioService.getAllUsersDecrypted()
        return ResponseEntity.ok(usuarios)
    }
}


