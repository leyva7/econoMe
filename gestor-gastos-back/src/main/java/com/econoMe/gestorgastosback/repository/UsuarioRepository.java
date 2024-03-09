package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
