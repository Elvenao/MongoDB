package com.example.model

data class Comentario(
    val userId: String,
    val userName: String,
    val comentario: String,
    val fecha: String? = null
)