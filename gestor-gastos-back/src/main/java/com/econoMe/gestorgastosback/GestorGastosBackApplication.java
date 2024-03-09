package com.econoMe.gestorgastosback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.econoMe.gestorgastosback.model.Usuario;
import com.econoMe.gestorgastosback.repository.UsuarioRepository;
@SpringBootApplication
public class GestorGastosBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestorGastosBackApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UsuarioRepository repository) {
        return args -> {
            Usuario usuario = new Usuario("Nombre", "Apellido", "correo@example.com", "contraseña");
            repository.save(usuario);
        };
    }
}
