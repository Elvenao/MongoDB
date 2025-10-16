package com.example.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "Categories")
data class Category(
    @Id val id: String? = null,
    var category: String? = null
)