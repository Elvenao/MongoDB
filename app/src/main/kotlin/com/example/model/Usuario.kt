package com.example.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Users")
data class Usuario(
    @Id val id: String? = null,
    var userName: String,
    var name: String,
    var biography: String? = null,
    var genres: List<String>? = null,
    var birthDate: String,
    var joiningDate: String,
    var password: String,
    var email: String,
    var avatar: String? = null,  
    var following: List<String>? = null,
    var followers: List<String>? = null,
    var like: List<String> = emptyList(),
    var dislike: List<String> = emptyList()
)