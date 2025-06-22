package com.example.controller

import com.example.model.Post
import com.example.model.PostResponse
import com.example.repository.PostRepository
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postRepository: PostRepository
) {
    @GetMapping
    fun getAll(): List<Post> = postRepository.findAll()

    @PostMapping("/create")
    fun crear(@RequestBody post: Post): ResponseEntity<Any> {
        println("HOLA")
        postRepository.save(post)
        return ResponseEntity.ok(PostResponse(
            true,
            "Post creado exitosamente"
        ))
    }
}


