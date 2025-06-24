package com.example.controller

import com.example.model.Multimedia
import com.example.repository.MultimediaRepository
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/multimedia")
class MultimediaController(
    private val multimediaRepository: MultimediaRepository
) {
    

    @GetMapping("/buscar")
    fun buscar(@RequestParam q: String): ResponseEntity<List<Multimedia>> {
        val query = "^${Regex.escape(q)}" // Escapar por seguridad
        val resultados = multimediaRepository.findByNameStartingWith(query)
        return ResponseEntity.ok(resultados)
    }

    
}


