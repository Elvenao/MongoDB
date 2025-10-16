package com.example.utils

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

object CryptoUtils {
    private val key = "8da949392%1!5423"
    fun decryptAES(base64Encrypted: String): String {
        val fullData = Base64.getDecoder().decode(base64Encrypted)
        val iv = fullData.sliceArray(0 until 16)
        val encryptedData = fullData.sliceArray(16 until fullData.size)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(iv))

        val decryptedBytes = cipher.doFinal(encryptedData)
        return String(decryptedBytes, Charsets.UTF_8)
    }
    fun encryptAES(plainText: String): String {

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = ByteArray(16).also { SecureRandom().nextBytes(it) } // IV aleatorio de 16 bytes
        val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val fullData = iv + encryptedBytes // Concatenamos IV + datos cifrados
        return Base64.getEncoder().encodeToString(fullData)
    }
}
