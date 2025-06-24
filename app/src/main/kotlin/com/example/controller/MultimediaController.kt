package com.example.controller

import com.example.model.Multimedia
import com.example.repository.MultimediaRepository
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity
import com.example.model.MultimediaIdImg

@RestController
@RequestMapping("/api/multimedia")
class MultimediaController(
    private val multimediaRepository: MultimediaRepository
) {
    

    @GetMapping("/buscar")
    fun buscar(@RequestParam q: String): ResponseEntity<List<Multimedia>> {
        val query = "^${Regex.escape(q)}" // Escapar por seguridad
        val resultados = multimediaRepository.findByNameStartingWith(query)
        println(resultados)
        return ResponseEntity.ok(resultados)
    }


    @PatchMapping("/idimg/{id}")
    fun getNameAndImgById(@PathVariable id: String): ResponseEntity<MultimediaIdImg> {
        println("ID: "+ id)
        val multimedia = multimediaRepository.findById(id).orElse(null) // ← válido si usas Optional
        val todas = multimediaRepository.findAll()
        println("Películas registradas:")
        todas.forEach { println("ID: '${it.id}', Name: '${it.name}'") }
        return if (multimedia != null) {
            ResponseEntity.ok(
                MultimediaIdImg(
                    multimedia.id,
                    multimedia.name,
                    multimedia.poster
                )
            )
        } else {
            println("ERRROR")
            ResponseEntity.notFound().build()
        }
    }
    
}


