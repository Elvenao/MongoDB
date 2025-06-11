package com.example.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Users")
data class Usuario(
    @Id val id: String? = null,
    val nombre: String,
    val edad: String
)