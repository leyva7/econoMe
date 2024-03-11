package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    public User createUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User getUserByID(Long id){
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No se encontró el usuario de ID: " + id));
    }

    public User updateUser(Long id, User userData) {
        User user = getUserByID(id);
        user.setName(userData.getName());
        user.setSurname(userData.getSurname());
        user.setMail(userData.getMail());

        return userRepository.save(user);
    }

    public void updatePassword(Long userId, String passwordUser, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (passwordEncoder.matches(passwordUser, user.getPassword())) {
            String codificatedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(codificatedPassword);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean validateCredentials(String mail, String password) {
        User user = userRepository.findByMail(mail);
        if (user != null) {
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

}