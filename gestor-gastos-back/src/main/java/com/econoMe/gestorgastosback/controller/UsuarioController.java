package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.model.Usuario;
import com.econoMe.gestorgastosback.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    // Buscar un usuario por ID
    public Usuario obtenerUsuarioPorId(Long id) {
    return usuarioRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException("No se pudo encontrar un usuario con el ID: " + id));
}


    public Usuario actualizarUsuario(Long id, Usuario detallesUsuario) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setName(detallesUsuario.getName());
        usuario.setSurname(detallesUsuario.getSurname());
        usuario.setMail(detallesUsuario.getMail());
        usuario.setPassword(detallesUsuario.getPassword());
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    } 
    
}

