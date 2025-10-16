package com.example.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.*

@Document(collection = "Multimedia")
data class Multimedia(
    @Id val id: String,
    @Indexed
    val name: String,
    val descripcion: String,
    val duracion: String,
    val director: String,
    val cast: List<String>,
    val gender: List<String>,
    val idMedia: String,
    val company: List<String>,
    val date: String,
    val poster: String,
    val rating: Float

)