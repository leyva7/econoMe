package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.exception.UserAlreadyExistsException;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;



import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
public class UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountingService accountingService;

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

    public boolean existByUsername(String username){
        return userRepository.existsByUsername(username);
    }

    public List<User> getAllUser(){
        return userRepository.findAll();
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("No se encontró el usuario de nombre: " + username));
    }

    public Optional<User> findById(Long id){
        return userRepository.findById(id);
    }

    public User updateDetails(Long id, UserDto userDto) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> new Exception("Usuario no encontrado con el id: " + id));

        user.setUsername(userDto.getUsername());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setMail(userDto.getMail());
        accountingService.findPersonalAccounting(user).get().setDescription("Contabilidad personal del usuario " + user.getUsername());

        return userRepository.save(user);
    }

    public void updatePassword(Long id, String passwordUser, String newPassword) {
        User user = userRepository.findById(id)
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

    public boolean validateCredentials(Long id, String password) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return true;
        }
        return false;
    }

}