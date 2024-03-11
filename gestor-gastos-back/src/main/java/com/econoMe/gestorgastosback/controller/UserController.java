package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.service.UserService;
import com.econoMe.gestorgastosback.exception.InvalidPasswordException;
import com.econoMe.gestorgastosback.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registro de usuario
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword())); // Codificamos la contraseña antes de guardarla
        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    // Actualización de datos del usuario
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    // Actualización de contraseña
    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordDetails) {
        String currentPassword = passwordDetails.get("currentPassword");
        String newPassword = passwordDetails.get("newPassword");
        try {
            userService.updatePassword(id, currentPassword, newPassword);
            return ResponseEntity.ok().build(); // Puedes devolver un mensaje de éxito si lo prefieres
        } catch (InvalidPasswordException e) {
            return ResponseEntity.badRequest().body("Contraseña actual incorrecta.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar la contraseña.");
        }
    }


    // Aquí puedes agregar otros endpoints que necesites, siguiendo el mismo patrón

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserByID(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }


    // Ejemplo: Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
