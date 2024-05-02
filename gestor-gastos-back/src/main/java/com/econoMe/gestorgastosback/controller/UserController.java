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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    private User getUserFromRequest(HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        return jwtService.getUserFromToken(token);
    }
    
    // Actualización de datos del usuario
    @PutMapping("/modifyDetails")
    public ResponseEntity<?> updateUserDetails(HttpServletRequest request, @RequestBody UserDto userDto) throws Exception {
        User user = getUserFromRequest(request);
        User updatedUser = userService.updateDetails(user.getId(), userDto);
        return ResponseEntity.ok(updatedUser);
    }

    // Actualización de contraseña
    @PutMapping("/modifyPassword")
    public ResponseEntity<?> updatePassword(Authentication authentication, @RequestBody Map<String, String> passwordDetails) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        String currentPassword = passwordDetails.get("currentPassword");
        String newPassword = passwordDetails.get("newPassword");
        try {
            userService.updatePassword(user.getId(), currentPassword, newPassword);
            return ResponseEntity.ok().build();
        } catch (InvalidPasswordException e) {
            return ResponseEntity.badRequest().body("Contraseña actual incorrecta.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al actualizar la contraseña.");
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        User user = userService.getUserByUsername(userDetails.getUsername());

        UserDto userDto = mappingService.userToDto(user);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUser();
        return ResponseEntity.ok(users);
    }

    // Ejemplo: Eliminar usuario
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }
        userService.deleteUser(user.getId());
        return ResponseEntity.ok().build();
    }
}
