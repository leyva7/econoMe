package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.dto.RegistrationDto;
import com.econoMe.gestorgastosback.dto.UserDto;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountingService accountingService;

    @Autowired
    private MappingService mappingService;

    /**
     * Registra un nuevo usuario y la primera contabilidad asociada.
     *
     * @param registrationDto DTO que contiene la información del usuario y la contabilidad a registrar.
     * @return DTO del usuario registrado.
     */
    @Transactional
    public UserDto registerUserAndAccounting(RegistrationDto registrationDto) {
        // Obtener el nuevo usuario del DTO
        User newUser = registrationDto.getUser();

        // Crear el usuario y guardar en la base de datos
        User savedUser = userService.createUser(newUser);

        // Obtener la nueva contabilidad del DTO y mapearla a la entidad Accounting
        Accounting newAccounting = mappingService.accountingDtoToAccounting(registrationDto.getAccounting());

        // Establecer al usuario creador de la contabilidad
        newAccounting.setUserCreator(savedUser);

        // Crear la primera contabilidad asociada al usuario
        accountingService.createFirstAccounting(newAccounting);

        // Mapear el usuario guardado a DTO y retornarlo
        return mappingService.userToDto(savedUser);
    }
}
