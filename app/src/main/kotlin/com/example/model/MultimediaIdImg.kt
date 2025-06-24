package com.example.model

import org.springframework.data.annotation.Id

import org.springframework.data.mongodb.core.index.Indexed

import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.*



@Document(collection = "Multimedia")
data class MultimediaIdImg(
    @Id
    var id: String,
    var name: String,
    var img: String
)