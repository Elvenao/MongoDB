package com.example.utils

import org.springframework.web.multipart.MultipartFile
import java.io.File
import com.example.locals.Local
import org.springframework.stereotype.Component
import java.lang.Runtime

@Component
class SaveResources {
    fun guardarImagenEnRutaFisica(file: MultipartFile, userId: String): String {
        val rutaAbsoluta = Local.getAbsoluteRouteForUsers()
        val carpeta = File(rutaAbsoluta)

        if (!carpeta.exists()) {
            carpeta.mkdirs()
        }

        val extension = file.originalFilename?.substringAfterLast('.', "") ?: "jpg"
        val filename = "avatar_$userId.$extension"
        val destinationFile = File(carpeta, filename)

        try {
            file.transferTo(destinationFile)
        } catch (e: Exception) {
            throw RuntimeException("Error al guardar el archivo: ${e.message}")
        }

        val comando = arrayOf(
            "cmd.exe", "/c",
            "icacls \"$destinationFile\" /grant Everyone:(R) /T"
        )


        println("Comando ejecutado: icacls \"${destinationFile.absolutePath}\" /grant IUSR:(R) /grant IIS_IUSRS:(R)")

        try {
            val proceso = Runtime.getRuntime().exec(comando)
            proceso.waitFor() // ✅ Espera a que termine el proceso
            if (proceso.exitValue() == 0) {
                println("Permisos asignados correctamente a ${destinationFile.name}")
            } else {
                println("⚠️ Error asignando permisos, código de salida: ${proceso.exitValue()}")
            }
        } catch (e: Exception) {
            println("❌ Error ejecutando icacls: ${e.message}")
        }

        return filename
    }
}  