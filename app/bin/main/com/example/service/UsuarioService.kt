package com.example.service

import com.example.model.Usuario
import com.example.repository.UsuarioRepository
import com.example.utils.CryptoUtils
import org.springframework.stereotype.Service



@Service
class UsuarioService(
    private val usuarioRepository: UsuarioRepository
) {
     // 🔐 clave de 16 bytes

    fun getAllUsersDecrypted(): List<Usuario> {
        return usuarioRepository.findAll().map { usuario ->
            usuario.copy(
                name = CryptoUtils.decryptAES(usuario.name),
                birthDate = CryptoUtils.decryptAES(usuario.birthDate),
                
            )
        }
    }

    fun getUserDecrypted(id: String): Usuario? {
        val usuario = usuarioRepository.findById(id).orElse(null)
        return usuario?.copy(
            name = CryptoUtils.decryptAES(usuario.name),
            birthDate = CryptoUtils.decryptAES(usuario.birthDate)
        )
    }
}
