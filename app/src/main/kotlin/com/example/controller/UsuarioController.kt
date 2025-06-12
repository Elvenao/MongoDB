package com.example.controller

import com.example.model.Usuario
import com.example.service.UsuarioService
import org.springframework.web.bind.annotation.*




@RestController
@RequestMapping("/api/users")
class UsuarioController(
    private val usuarioService: UsuarioService
) {
    @GetMapping
    fun getAll(): List<Usuario> = usuarioService.getAllUsersDecrypted()
    /* 
    @PostMapping
    fun crear(@RequestBody usuario: Usuario): Usuario = usuarioService.getAllUsersDecrypted(usuario)
    */
}


