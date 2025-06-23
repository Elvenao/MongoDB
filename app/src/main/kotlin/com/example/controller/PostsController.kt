package com.example.controller

import com.example.model.Post
import com.example.model.PostResponse
import com.example.repository.PostRepository
import com.example.repository.UsuarioRepository
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

data class PostWithAvatar(
    val post: Post,
    val userAvatar: String?
)

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postRepository: PostRepository,
    private val usuarioRepository: UsuarioRepository
) {
    @GetMapping
    fun getAll(): List<PostWithAvatar> {
        val posts = postRepository.findAll()
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
}

