package com.example.repository

import com.example.model.Post
import org.springframework.data.mongodb.repository.MongoRepository

interface PostRepository : MongoRepository<Post, String>
