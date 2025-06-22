package com.example.controller

import com.example.model.Category
import com.example.repository.CategoryRepository
import org.springframework.web.bind.annotation.*
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryRepository: CategoryRepository
) {
    @GetMapping
    fun getAll(): List<Category> = categoryRepository.findAll()
    
}


