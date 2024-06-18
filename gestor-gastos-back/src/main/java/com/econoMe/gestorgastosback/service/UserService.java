package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.exception.UserException;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountingService accountingService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, AccountingService accountingService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.accountingService = accountingService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param user Usuario a crear.
     * @return Usuario creado.
     * @throws UserException si el nombre de usuario o el correo electrónico ya están en uso.
     */
    public User createUser(User user) {
        // Verificar si el nombre de usuario ya está en uso
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserException("El nombre de usuario ya está en uso");
        }

        // Verificar si el correo electrónico ya está en uso
        if (userRepository.existsByMail(user.getMail())) {
            throw new UserException("El correo electrónico ya está en uso");
        }

        return userRepository.save(user);
    }

    /**
     * Obtiene todos los usuarios.
     *
     * @return Lista de todos los usuarios.
     */
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    /**
     * Obtiene un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario del usuario buscado.
     * @return Usuario encontrado.
     * @throws UserException si no se encuentra ningún usuario con el nombre de usuario especificado.
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("No se encontró el usuario con nombre de usuario: " + username));
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id ID del usuario.
     * @return Usuario encontrado.
     * @throws UserException si no se encuentra ningún usuario con el ID especificado.
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException("El usuario con id " + id + " no ha sido encontrado."));
    }

    /**
     * Actualiza los detalles de un usuario.
     *
     * @param id      ID del usuario a actualizar.
     * @param userDto DTO con los datos actualizados del usuario.
     * @return Usuario actualizado.
     * @throws UserException si el nuevo nombre de usuario o correo electrónico ya están en uso por otro usuario.
     */
    public User updateDetails(Long id, UserDto userDto) {
        User user = findById(id);

        // Verificar si el nuevo nombre de usuario ya está en uso por otro usuario
        if (!user.getUsername().equals(userDto.getUsername()) && userRepository.existsByUsername(userDto.getUsername())) {
            throw new UserException("El nombre de usuario ya está en uso");
        }

        // Verificar si el nuevo correo electrónico ya está en uso por otro usuario
        if (!user.getMail().equals(userDto.getMail()) && userRepository.existsByMail(userDto.getMail())) {
            throw new UserException("El correo electrónico ya está en uso");
        }

        // Actualizar los datos del usuario
        user.setUsername(userDto.getUsername());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setMail(userDto.getMail());

        // Actualizar la descripción de la contabilidad personal del usuario
        accountingService.findPersonalAccounting(user)
                .orElseThrow(() -> new UserException("No se encontró la contabilidad personal para el usuario: " + user.getUsername()))
                .setDescription("Contabilidad personal del usuario " + user.getUsername());

        return userRepository.save(user);
    }

    /**
     * Actualiza la contraseña de un usuario.
     *
     * @param id           ID del usuario.
     * @param passwordUser Contraseña actual del usuario.
     * @param newPassword  Nueva contraseña a establecer.
     * @throws UserException si la contraseña actual es incorrecta.
     */
    public void updatePassword(Long id, String passwordUser, String newPassword) {
        User user = findById(id);

        // Verificar si la contraseña actual es correcta
        if (!passwordEncoder.matches(passwordUser, user.getPassword())) {
            throw new UserException("La contraseña actual es incorrecta.");
        }

        // Codificar y establecer la nueva contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id ID del usuario a eliminar.
     * @throws UserException si no se encuentra ningún usuario con el ID especificado.
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserException("El usuario con id " + id + " no existe.");
        }
        userRepository.deleteById(id);
    }
}
