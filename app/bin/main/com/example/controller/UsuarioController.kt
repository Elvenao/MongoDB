package com.example.controller

import com.example.model.Usuario
import com.example.model.LoginResponse
import com.example.model.UserIdImg
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

    @GetMapping("/users/buscar")
    fun buscar(@RequestParam q: String): ResponseEntity<List<Usuario>> {
        val query = "^${Regex.escape(q)}" // Escapar por seguridad
        val resultados = usuarioRepository.findByUserNameStartingWith(query)
        
        return ResponseEntity.ok(resultados)
    }

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
        usuarioRepository.save(usuario)
        println("Usuario guardado: ${usuario.userName} con email: ${usuario.email}")
        
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


    @PostMapping("/users/follow/toggle")
    fun toggleFollow(
        @RequestParam target: String,   // usuario a seguir/dejar de seguir
        @RequestParam follower: String  // quien sigue / deja de seguir
    ): ResponseEntity<Void> {
        val user = usuarioRepository.findById(target).orElse(null)
        val followerUser = usuarioRepository.findById(follower).orElse(null)

        if (user == null || followerUser == null) {
            return ResponseEntity.notFound().build()
        }

        // Toggle followers en el usuario objetivo
        val followersSet = (user.followers ?: emptyList()).toMutableSet()
        if (followersSet.contains(follower)) {
            followersSet.remove(follower)
        } else {
            followersSet.add(follower)
        }
        val updatedUser = user.copy(followers = followersSet.toList())
        usuarioRepository.save(updatedUser)

        // Toggle following en el usuario que sigue
        val followingSet = (followerUser.following ?: emptyList()).toMutableSet()
        if (followingSet.contains(target)) {
            followingSet.remove(target)
        } else {
            followingSet.add(target)
        }
        val updatedFollower = followerUser.copy(following = followingSet.toList())
        usuarioRepository.save(updatedFollower)

        return ResponseEntity.ok().build()
    }

    @GetMapping("/users/username/{id}")
    fun getUserIdImgById(@PathVariable id: String): ResponseEntity<UserIdImg> {
        val usuario = usuarioRepository.findById(id).orElse(null)
        return if (usuario != null) {
            
            ResponseEntity.ok(
                UserIdImg(
                    id = usuario.id ?: "",
                    username = usuario.userName,
                    img = usuario.avatar ?: ""
                )
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PatchMapping("/users/like/{id}")
    fun setLike(
        @PathVariable id: String, //User ID
        @RequestParam mediaId: String   //Media ID
    ): ResponseEntity<LoginResponse> {
        val user = usuarioRepository.findById(id).orElse(null)
        println("MediaId : ${mediaId}")
        if (user != null) {
            val likeSet = (user.like ?: emptyList()).toMutableSet()
            val disLikeSet = (user.dislike ?: emptyList()).toMutableSet()

            if(disLikeSet.contains(mediaId)){
                println("Esta en dislikes!!!")
                disLikeSet.remove(mediaId)
                 
            }

            val message = if (likeSet.contains(mediaId)) {
                likeSet.remove(mediaId)
                "Unliked"
            } else {
                likeSet.add(mediaId)
                "Liked"
            }

            val updatedUser = user.copy(
                dislike = disLikeSet.toList(),
                like = likeSet.toList()
            )
            usuarioRepository.save(updatedUser)

            return ResponseEntity.ok(LoginResponse(true, message))
        } else {
            return ResponseEntity.ok(LoginResponse(false, "Usuario no encontrado"))
        }
    }

    @PatchMapping("/users/dislike/{id}")
    fun disLike(
        @PathVariable id: String,
        @RequestParam mediaId: String
    ): ResponseEntity<LoginResponse> {
        val user = usuarioRepository.findById(id).orElse(null)
        println("MediaId : ${mediaId}")
        if (user != null) {
            val likeSet = (user.like ?: emptyList()).toMutableSet()
            val disLikeSet = (user.dislike ?: emptyList()).toMutableSet()

            if(likeSet.contains(mediaId)){
                println("Esta en likes!!!")
                likeSet.remove(mediaId)
                
            }

            val message = if (disLikeSet.contains(mediaId)) {
                disLikeSet.remove(mediaId)
                "Undisliked"
            } else {
                disLikeSet.add(mediaId)
                "Disliked"
            }
            
            val updatedUser = user.copy(
                dislike = disLikeSet.toList(),
                like = likeSet.toList()
            )
            usuarioRepository.save(updatedUser)
            return ResponseEntity.ok(LoginResponse(true, message))
        } else {
            return ResponseEntity.ok(LoginResponse(false, "Usuario no encontrado"))
        }
    }
/* 
    @PatchMapping("/users/email/{id}")
    fun updateEmail(
        @PathVariable id: String,
        @RequestParam newEmail: String
    ): ResponseEntity<LoginResponse> {
        val p = newEmail
        val existing = usuarioRepository.findById(id).orElse(null)

        return if (existing != null) {
            val updated = existing.copy(email = p)
            usuarioRepository.save(updated)
            ResponseEntity.ok(LoginResponse(true, "Email actualizadas"))
        } else {
            ResponseEntity.ok(LoginResponse(false, "Email no actualizadas"))
        }
        
    }

    @PatchMapping("/users/password/{id}")
    fun updatePassword(
        @PathVariable id: String,
        @RequestParam newPassword: String
    ): ResponseEntity<LoginResponse> {
        val p = HashingUtils.hashingBCrypt(newPassword)
        val existing = usuarioRepository.findById(id).orElse(null)
        

        return if (existing != null) {
            val updated = existing.copy(password = p)
            usuarioRepository.save(updated)
            ResponseEntity.ok(LoginResponse(true, "Password actualizadas"))
        } else {
            ResponseEntity.ok(LoginResponse(false, "Password no actualizadas"))
        }
        
    }
    
    @PatchMapping("/users/updateusername/{id}")
    fun updateUserName(
        @PathVariable id: String,
        @RequestParam newUserName: String
    ): ResponseEntity<LoginResponse> {
        val p = newUserName
        val existing = usuarioRepository.findById(id).orElse(null)

        if (existing != null) {
            println("Encontrado")
            val updated = existing.copy(userName = p)
            usuarioRepository.save(updated)
            return ResponseEntity.ok(LoginResponse(true, "userName actualizadas"))
        } else {
            println("NO ENCONTRADO")
            return ResponseEntity.ok(LoginResponse(false, "userName no actualizadas"))
        }
        
    }

   

    

    
*/    

}


