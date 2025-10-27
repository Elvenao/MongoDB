package com.example.controller

import com.example.model.Post
import com.example.model.PostResponse
import com.example.repository.PostRepository
import com.example.repository.UsuarioRepository
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import com.example.model.Comentario
import com.example.model.LikeInformation



data class PostWithAvatar(
    val post: Post,
    val userAvatar: String?
)
data class NuevoComentarioRequest(
    val userId: String,
    val userName: String,
    val comentario: String
)

data class LikeInformationUptaded(
    val message: String,
    val likes: Long,
    val likesList: List<LikeInformation>
)

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postRepository: PostRepository,
    private val usuarioRepository: UsuarioRepository
) {
    @GetMapping
    fun getAll(): List<PostWithAvatar> {
    val posts = postRepository.findAll().reversed()
    return posts.map { post ->
        val user = post.userId?.let { usuarioRepository.findById(it).orElse(null) }
        PostWithAvatar(
            post = post,
            userAvatar = user?.avatar
        )
        }
    }

    @GetMapping("/details")
    fun verPost(@RequestParam id: String): ResponseEntity<PostWithAvatar?> {
        val post = postRepository.findById(id)
        return if (post.isPresent) {
            val user = usuarioRepository.findById(post.get().userId).orElse(null)
            ResponseEntity.ok(
                PostWithAvatar(
                    post = post.get(),
                    userAvatar = user?.avatar
                )
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }
    @PostMapping("/{id}/comment")
    fun agregarComentario(
        @PathVariable id: String,
        @RequestBody request: NuevoComentarioRequest
    ): ResponseEntity<PostWithAvatar?> {
        val postOpt = postRepository.findById(id)
        if (postOpt.isPresent) {
            val post = postOpt.get()
            val nuevoComentario = Comentario(
                userId = request.userId,
                userName = request.userName,
                comentario = request.comentario,
                fecha = java.time.LocalDateTime.now().toString()
            )
            val comentariosActualizados = post.comments + nuevoComentario
            val postActualizado = post.copy(comments = comentariosActualizados)
            postRepository.save(postActualizado)

            val user = usuarioRepository.findById(post.userId).orElse(null)
            return ResponseEntity.ok(
                PostWithAvatar(
                    post = postActualizado,
                    userAvatar = user?.avatar
                )
            )
        }
        return ResponseEntity.notFound().build()
    }
    @PostMapping("/create")
    fun crear(@RequestBody post: Post): ResponseEntity<Any> {
        
        postRepository.save(post)
        println("Post Created")
        return ResponseEntity.ok(PostResponse(
            true,
            "Post creado exitosamente"
        ))
    }
    
    @PostMapping("/like/{id}")
    fun likePost(@PathVariable id: String, @RequestBody likesInformation: LikeInformation): ResponseEntity<Any> {
        val post = postRepository.findById(id).orElse(null) 
            ?: return ResponseEntity.status(404).body("Post no encontrado")
        
        val likeSet = (post.likesInformation ?: emptyList()).toMutableSet()
        val existingLike = likeSet.find { it.userId == likesInformation.userId }
        var message: String
        if(existingLike != null){
            likeSet.remove(existingLike)
            post.likes = post.likes - 1
            message = "Disliked"
        }else{
            likeSet.add(likesInformation)
            post.likes = post.likes + 1
            message = "Liked"
        }
        post.likesInformation = likeSet.toList()
        postRepository.save(post)

        
        return ResponseEntity.ok(
            LikeInformationUptaded(
                message, post.likes, post.likesInformation
            )
        )
    }

    @GetMapping("/users/{idUser}/posts")
    fun getUserPosts(@PathVariable idUser: String): ResponseEntity<List<PostWithAvatar>> {
        val posts = postRepository.findAll().filter { it.userId == idUser }
        val user = usuarioRepository.findById(idUser).orElse(null)
        val postsWithAvatar = posts.map { post ->
            PostWithAvatar(
                post = post,
                userAvatar = user?.avatar
            )
        }
        return ResponseEntity.ok(postsWithAvatar)
    }
}
