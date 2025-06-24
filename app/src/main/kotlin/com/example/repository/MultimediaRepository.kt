package com.example.repository

import com.example.model.Multimedia
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.core.mapping.*
import org.springframework.data.mongodb.repository.Query


interface MultimediaRepository : MongoRepository<Multimedia, String>{

    @Query("{ 'name': { \$regex: ?0, \$options: 'i' } }")
    fun findByNameStartingWith(texto: String): List<Multimedia>
}
