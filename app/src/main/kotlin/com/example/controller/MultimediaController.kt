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
        
        return ResponseEntity.ok(resultados)
    }

    
    @GetMapping("/idimg")
    fun getNameAndImgById(@RequestParam id: String): ResponseEntity<MultimediaIdImg?> {
        println("ID: "+ id)
        val multimedia = multimediaRepository.findById(id).orElse(null) // ← válido si usas Optional
        
        
        return if (multimedia != null) {
            ResponseEntity.ok(
                MultimediaIdImg(
                    id = multimedia.id,
                    name = multimedia.name,
                    img = multimedia.poster
                )
            )
        } else {
            println("ERRROR")
            ResponseEntity.notFound().build()
        }
    }
    
    

    @GetMapping("/{id}")
    fun getMultimediaDetails(@PathVariable id: String): ResponseEntity<List<Multimedia>> {
        
        val multimedia = multimediaRepository.findById(id)
        println("IDDD>"+id)
        println(multimedia)
        return if (multimedia.isPresent) {
            ResponseEntity.ok(listOf(multimedia.get()))
        } else {
            ResponseEntity.notFound().build()
        }
    }
}


