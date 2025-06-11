package com.example.controller

import com.example.model.Usuario
import com.example.repository.UsuarioRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UsuarioController(
    private val usuarioRepository: UsuarioRepository
) {
    @GetMapping
    fun getAll(): List<Usuario> = usuarioRepository.findAll()

    @PostMapping
    fun crear(@RequestBody usuario: Usuario): Usuario = usuarioRepository.save(usuario)
}


