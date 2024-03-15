package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.dto.AuthResponse;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.service.AuthService;
import com.econoMe.gestorgastosback.service.UserService;
import com.econoMe.gestorgastosback.exception.InvalidPasswordException;
import com.econoMe.gestorgastosback.dto.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    
    // Actualización de datos del usuario
    @PutMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(username, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // Actualización de contraseña
    @PutMapping("/{username}/password")
    public ResponseEntity<?> updatePassword(@PathVariable String username, @RequestBody Map<String, String> passwordDetails) {
        String currentPassword = passwordDetails.get("currentPassword");
        String newPassword = passwordDetails.get("newPassword");
        try {
            userService.updatePassword(username, currentPassword, newPassword);
            return ResponseEntity.ok().build(); // Puedes devolver un mensaje de éxito si lo prefieres
        } catch (InvalidPasswordException e) {
            return ResponseEntity.badRequest().body("Contraseña actual incorrecta.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar la contraseña.");
        }
    }


    // Aquí puedes agregar otros endpoints que necesites, siguiendo el mismo patrón

    @GetMapping("/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    // Ejemplo: Eliminar usuario
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }
}
