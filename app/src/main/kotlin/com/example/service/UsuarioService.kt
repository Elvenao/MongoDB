package com.example.service

import com.example.model.Usuario
import com.example.repository.UsuarioRepository
import com.example.utils.CryptoUtils
import org.springframework.stereotype.Service



@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository
) {
    private val key = "8da949392%1!5423" // 🔐 clave de 16 bytes

    fun getAllUsersDecrypted(): List<Usuario> {
        return usuarioRepository.findAll().map { usuario ->
            usuario.copy(
                userName = CryptoUtils.decryptAES(usuario.userName, key),
                name = CryptoUtils.decryptAES(usuario.name, key),
                birthDate = CryptoUtils.decryptAES(usuario.birthDate, key),
                email = CryptoUtils.decryptAES(usuario.email, key),
                avatar = usuario.avatar
            )
        }
    }
}
