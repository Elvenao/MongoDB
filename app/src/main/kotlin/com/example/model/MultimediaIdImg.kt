package com.example.model

import org.springframework.data.annotation.Id

import org.springframework.data.mongodb.core.index.Indexed

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.*



@Document(collection = "Multimedia")
data class MultimediaIdImg(
    @Id
    val id: String,
    val name: String,
    val img: String
)