package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.exception.UserException;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService{

    private final UserRepository userRepository;
    private final AccountingService accountingService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, AccountingService accountingService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountingService = accountingService;
        this.passwordEncoder = passwordEncoder;
    }
    public User createUser(User user){
        // Verificar si el nombre de usuario ya está en uso
        if (userRepository.existsByUsername(user.getUsername())) {
            System.out.println("aaaddd");
            throw new UserException("El nombre de usuario ya está en uso");
        }

        // Verificar si el correo electrónico ya está en uso
        if (userRepository.existsByMail(user.getMail())) {
            throw new UserException("El correo electrónico ya está en uso");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword())); // Asegúrate de codificar la contraseña antes de guardarla.
        return userRepository.save(user);
    }

    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("No se encontró el usuario con nombre de usuario: " + username));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException("El usuario con id " + id + " no ha sido encontrado."));
    }

    public User updateDetails(Long id, UserDto userDto) {
        User user = findById(id);
        user.setUsername(userDto.getUsername());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setMail(userDto.getMail());
        accountingService.findPersonalAccounting(user)
                .orElseThrow(() -> new UserException("No se encontró la contabilidad personal para el usuario: " + user.getUsername()))
                .setDescription("Contabilidad personal del usuario " + user.getUsername());

        return userRepository.save(user);
    }

    public void updatePassword(Long id, String passwordUser, String newPassword) {
        User user = findById(id);

        if (!passwordEncoder.matches(passwordUser, user.getPassword())) {
            throw new UserException("La contraseña actual es incorrecta.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserException("El usuario con id " + id + " no existe.");
        }
        userRepository.deleteById(id);
    }

}