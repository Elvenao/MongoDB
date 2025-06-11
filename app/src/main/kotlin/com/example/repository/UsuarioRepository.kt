package com.example.repository

import com.example.model.Usuario
import org.springframework.data.mongodb.repository.MongoRepository

interface UsuarioRepository : MongoRepository<Usuario, String>
