package com.econoMe.gestorgastosback.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Inicia la configuración de autorización de solicitudes
                .authorizeHttpRequests(authorize -> authorize
                        // Configura rutas específicas
                        .requestMatchers("/api/users/all").permitAll() // Permite el acceso sin autenticación a /api/users/all
                        .requestMatchers("/home", "/").permitAll() // Ejemplo adicional, ajusta según tus necesidades
                        .anyRequest().authenticated() // Requiere autenticación para cualquier otra solicitud
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .permitAll()
                )
                .cors(Customizer.withDefaults()); // Habilita CORS con configuración predeterminada

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


