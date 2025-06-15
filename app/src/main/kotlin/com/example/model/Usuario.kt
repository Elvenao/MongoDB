package com.example.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Users")
data class Usuario(
    @Id val id: String? = null,
    val userName: String,
    val name: String,
    val birthDate: String,
    val biography: String,
    val genres: List<*>,
    val email: String,
    val password: String,
    val avatar: String  

)