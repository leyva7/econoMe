package com.econoMe.gestorgastosback.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id; // ID del usuario

    @Column(nullable = false, unique = true)
    private String username; // Nombre de usuario

    @Column(nullable = false)
    private String name; // Nombre del usuario

    @Column(nullable = false)
    private String surname; // Apellido del usuario

    @Column(nullable = false, unique = true)
    private String mail; // Correo electrónico del usuario

    @Column(nullable = false)
    private String password; // Contraseña del usuario

    // Implementación de los métodos de UserDetails

    @Override
    @JsonIgnore // Ignora este método en la serialización JSON
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")); // Devuelve el rol del usuario
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true; // Indica si la cuenta del usuario no ha expirado
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true; // Indica si la cuenta del usuario no está bloqueada
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true; // Indica si las credenciales del usuario no han expirado
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true; // Indica si la cuenta del usuario está habilitada
    }
}
