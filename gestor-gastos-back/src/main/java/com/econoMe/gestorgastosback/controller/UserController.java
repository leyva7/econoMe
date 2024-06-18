package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.service.JwtService;
import com.econoMe.gestorgastosback.service.MappingService;
import com.econoMe.gestorgastosback.service.UserService;
import com.econoMe.gestorgastosback.exception.InvalidPasswordException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private JwtService jwtService;

    // Método privado para obtener el usuario desde la solicitud HTTP utilizando JWT
    private User getUserFromRequest(HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        return jwtService.getUserFromToken(token);
    }

    // Endpoint para actualizar detalles del usuario
    @PutMapping("/modifyDetails")
    public ResponseEntity<?> updateUserDetails(HttpServletRequest request, @RequestBody UserDto userDto) throws Exception {
        User user = getUserFromRequest(request);
        User updatedUser = userService.updateDetails(user.getId(), userDto);
        return ResponseEntity.ok(updatedUser);
    }

    // Endpoint para actualizar la contraseña del usuario
    @PutMapping("/modifyPassword")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody Map<String, String> passwordDetails) {
        // Verificar la autenticación y obtener el usuario
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        // Obtener las contraseñas actuales y nueva del cuerpo de la solicitud
        String currentPassword = passwordDetails.get("currentPassword");
        String newPassword = passwordDetails.get("newPassword");

        try {
            // Intentar actualizar la contraseña
            userService.updatePassword(user.getId(), currentPassword, newPassword);
            return ResponseEntity.ok().build();
        } catch (InvalidPasswordException e) {
            return ResponseEntity.badRequest().body("Contraseña actual incorrecta.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar la contraseña.");
        }
    }

    // Endpoint para obtener el perfil del usuario actual
    @GetMapping("/details")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        // Verificar la autenticación y obtener los detalles del usuario
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        User user = userService.getUserByUsername(userDetails.getUsername());

        UserDto userDto = mappingService.userToDto(user);
        return ResponseEntity.ok(userDto);
    }

    // Endpoint para obtener todos los usuarios
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    // Ejemplo: Endpoint para eliminar un usuario
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        // Verificar la autenticación y eliminar al usuario
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }
        userService.deleteUser(user.getId());
        return ResponseEntity.ok().build();
    }
}
