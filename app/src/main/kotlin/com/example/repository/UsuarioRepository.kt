package com.example.repository

import com.example.model.Usuario
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.core.mapping.*
import org.springframework.data.mongodb.repository.Query

interface UsuarioRepository : MongoRepository<Usuario, String>{
    fun findByEmail(email: String): Usuario?
    fun findByUserName(userName: String): Usuario?

    @Query("{ 'userName': { \$regex: ?0, \$options: 'i' } }")
    fun findByUserNameStartingWith(texto: String): List<Usuario>
    
}
