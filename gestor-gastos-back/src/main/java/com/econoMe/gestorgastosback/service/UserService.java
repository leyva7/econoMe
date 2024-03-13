package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.exception.UserAlreadyExistsException;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;



import java.util.List;
import java.util.NoSuchElementException;


@Service
public class UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public User createUser(User user) throws UserAlreadyExistsException {
        // Verificar si el correo electrónico ya está en uso
        if (userRepository.existsByMail(user.getMail())) {
            throw new UserAlreadyExistsException("El correo electrónico ya está en uso");
        }

        // Verificar si el nombre de usuario ya está en uso
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("El nombre de usuario ya está en uso");
        }

        return userRepository.save(user);
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("No se encontró el usuario de ID: " + username));
    }

    public User updateUser(String username, User user){
        if(user.getUsername() == null || !userRepository.existsById(username)){
            throw new IllegalStateException("La cuenta con el nombre de usuario " + user.getUsername() + " no existe.");
        }

        return userRepository.save(user);
    }

    public void updatePassword(String username, String passwordUser, String newPassword) {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (passwordEncoder.matches(passwordUser, user.getPassword())) {
            String codificatedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(codificatedPassword);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
    }

    public void deleteUser(String username) {
        userRepository.deleteById(username);
    }

    public boolean validateCredentials(String mail, String password) {
        User user = userRepository.findByMail(mail);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

}