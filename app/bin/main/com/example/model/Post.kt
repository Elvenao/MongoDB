package com.example.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Posts")
data class Post(
    @Id val id: String? = null,
    val user: String,
    val userId: String,
    val title: String,
    val content: String,
    val date: String,
    val time: String
)