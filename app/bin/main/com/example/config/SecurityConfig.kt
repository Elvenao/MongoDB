package com.example.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            // 1) Deshabilita CSRF para que los POST/PUT/DELETE no requieran token de formulario
            .csrf { it.disable() }

            // 2) Permite el acceso libre a TODAS las rutas
            .authorizeHttpRequests { authz ->
                authz.anyRequest().permitAll()
            }

            // 3) Si no usas sesiones, mantenlo stateless
            .sessionManagement { sess ->
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }

            // 4) Desactiva el formLogin y el HttpBasic
            .formLogin { it.disable() }
            .httpBasic { it.disable() }

        return http.build()
    }
}
