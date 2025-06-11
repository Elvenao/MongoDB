package com.example.controller

import com.example.model.Post
import com.example.repository.PostRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val postRepository: PostRepository
) {
    @GetMapping
    fun getAll(): List<Post> = postRepository.findAll()

    @PostMapping
    fun crear(@RequestBody post: Post): Post = postRepository.save(post)
}


