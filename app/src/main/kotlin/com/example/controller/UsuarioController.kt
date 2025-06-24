package com.example.controller

import com.example.model.Usuario
import com.example.model.LoginResponse
import com.example.repository.UsuarioRepository
import com.example.service.UsuarioService
import com.example.utils.JwtUtil
import com.example.utils.HashingUtils
import com.example.utils.CryptoUtils
import com.example.model.UserNameDto
import com.example.model.UpdateCategoriesRequest
import com.example.utils.SaveResources

import org.springframework.web.multipart.MultipartFile
import java.io.File
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus
import java.net.InetAddress



@RestController
@RequestMapping("/api")
class UsersController(
    private val usuarioService: UsuarioService,
    private val usuarioRepository: UsuarioRepository,
    private val saveResources: SaveResources,
    
) {

    @GetMapping("/users")
    fun listAll(): ResponseEntity<List<Usuario>> {
        val usuarios: List<Usuario> = usuarioService.getAllUsersDecrypted()
        return ResponseEntity.ok(usuarios)
    }

    @PatchMapping("users/avatar/{id}")
    fun updateAvatar(
        @PathVariable id: String,
        @RequestPart image: MultipartFile
    ): ResponseEntity<LoginResponse>{
        val user = usuarioRepository.findById(id).orElse(null)
        if (user != null) {
            val ruta = saveResources.guardarImagenEnRutaFisica(image, user.id!!)
            val rutaCompleta = "/Images/Users/"+ ruta
            val updated = user.copy(avatar = rutaCompleta)
            usuarioRepository.save(updated)
            println("Imagen guardada en: $ruta")

            return ResponseEntity.ok(LoginResponse( true, "Avatar actualizado",))
        }
        return ResponseEntity.ok(LoginResponse( false,"Hubo problemas intentelo mas tarde"))
    }

    @PatchMapping("users/categories/{id}")
    fun updateCategories(
        @PathVariable id: String,
        @RequestBody update: UpdateCategoriesRequest
    ): ResponseEntity<LoginResponse> {
        val p = update.categories
        val existing = usuarioRepository.findById(id).orElse(null)

        return if (existing != null) {
            val updated = existing.copy(genres = p)
            usuarioRepository.save(updated)
            ResponseEntity.ok(LoginResponse(true, "Categorías actualizadas"))
        } else {
            ResponseEntity.ok(LoginResponse(false, "Categorías no actualizadas"))
        }
        
    }

    @PostMapping("/signup/userName")
    fun repeteadUserName(@RequestBody userName: UserNameDto): ResponseEntity<Any>{
        val repeteadUserName = usuarioRepository.findByUserName(userName.userName)
        println(userName)
        println(repeteadUserName)
        if(repeteadUserName != null){
            println("Repetido")
            return ResponseEntity.ok(
                LoginResponse(
                    false,
                    "Nombre de usuario ya registrado"
                )
            )
        }
        println("No Repetido")
        return ResponseEntity.ok(LoginResponse(
            true,
            "Nombre de usuario no registrado"
        ))
    }
    
    @PostMapping("/signup")
    fun signUp(@RequestBody usuario: Usuario): ResponseEntity<Any>{
        val repeteadEmail = usuarioRepository.findByEmail(usuario.email)
        println(usuario.email)
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
        usuario.name = CryptoUtils.encryptAES(usuario.name)
        usuario.birthDate = CryptoUtils.encryptAES(usuario.birthDate)
        val saverdUser = usuarioRepository.save(usuario)
        println("Usuario guardado: ${usuario.id} con email: ${usuario.email}")
        val accessToken = JwtUtil.generateAccessToken(usuario)
        val refreshToken = JwtUtil.generateRefreshToken(usuario)
        return ResponseEntity.ok(LoginResponse(
            true,
            "Registro Exitoso",
            accessToken,
            refreshToken
        ))
    }

    @GetMapping("/users/{id}")
    fun getUserProfile(@PathVariable id: String): ResponseEntity<Usuario?> {
        val usuario = usuarioService.getUserDecrypted(id)
        return if (usuario != null) {
            ResponseEntity.ok(usuario)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}


