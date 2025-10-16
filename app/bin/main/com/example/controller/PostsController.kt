package com.example.controller

import com.example.model.Post
import com.example.model.PostResponse
import com.example.repository.PostRepository
import com.example.repository.UsuarioRepository
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import com.example.model.Comentario



data class PostWithAvatar(
    val post: Post,
    val userAvatar: String?
)
data class NuevoComentarioRequest(
    val userId: String,
    val userName: String,
    val comentario: String
)

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postRepository: PostRepository,
    private val usuarioRepository: UsuarioRepository
) {
    @GetMapping
    fun getAll(): List<PostWithAvatar> {
        val posts = postRepository.findAll().reversed() // <- invierte la lista
        return posts.map { post ->
            val user = usuarioRepository.findById(post.userId).orElse(null)
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
        println("HOLA")
        postRepository.save(post)
        return ResponseEntity.ok(PostResponse(
            true,
            "Post creado exitosamente"
        ))
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
