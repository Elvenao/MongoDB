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

    @GetMapping("/idimg")
    fun getNameAndImgById(@RequestParam id: String): ResponseEntity<MultimediaIdImg> {
        val multimedia = multimediaRepository.findById(id).orElse(null)
        return if (multimedia != null) {
            ResponseEntity.ok(
                MultimediaIdImg(
                    id = multimedia.id,
                    name = multimedia.name,
                    img = multimedia.poster
                )
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
}


