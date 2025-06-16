package com.example.controller

import com.example.model.Usuario
import com.example.model.LoginResponse
import com.example.repository.UsuarioRepository
import com.example.service.UsuarioService
import com.example.utils.JwtUtil
import com.example.utils.HashingUtils
import com.example.utils.CryptoUtils


import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus



@RestController
@RequestMapping("/api")
class UsersController(
    private val usuarioService: UsuarioService,
    private val usuarioRepository: UsuarioRepository
) {

    @GetMapping("/users")
    fun listAll(): ResponseEntity<List<Usuario>> {
        val usuarios: List<Usuario> = usuarioService.getAllUsersDecrypted()
        return ResponseEntity.ok(usuarios)
    }

    @PostMapping("/signup")
    fun signUp(@RequestBody usuario: Usuario): ResponseEntity<Any>{
        val repeteadEmail = usuarioRepository.findByEmail(usuario.email)
        if(repeteadEmail != null){
            println("Email repetido")
            return ResponseEntity.ok(
                LoginResponse(
                    false,
                    "Correo electronico ya registrado"
                )
            )
        }
        println("Paso")
        if(usuario.password.trim().length < 8)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La contraseña debe de tener una longitud mayor a 8 caracteres")
        usuario.password = HashingUtils.hashingBCrypt(usuario.password)
        usuario.userName = CryptoUtils.encryptAES(usuario.userName)
        usuario.name = CryptoUtils.encryptAES(usuario.name)
        usuario.birthDate = CryptoUtils.encryptAES(usuario.birthDate)
        usuarioRepository.save(usuario)
        val accessToken = JwtUtil.generateAccessToken(usuario)
        val refreshToken = JwtUtil.generateRefreshToken(usuario)
        return ResponseEntity.ok(LoginResponse(
            true,
            "Registro Exitoso",
            accessToken,
            refreshToken
        ))
    }
}


