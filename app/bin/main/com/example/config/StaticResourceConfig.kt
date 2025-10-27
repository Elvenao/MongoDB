package com.example.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class StaticResourceConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // "/Images/**" será la URL pública
        registry.addResourceHandler("/Images/**")
            // Ruta real en tu PC donde están las imágenes
            .addResourceLocations("file:///C:/Users/emhdz/Documents/Programacion%20Movil/Imagenes/")
    }
}
