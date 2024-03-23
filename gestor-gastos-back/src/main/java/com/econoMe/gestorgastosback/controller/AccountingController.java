package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.dto.AccountingDto;
import com.econoMe.gestorgastosback.dto.AccountingRegistration;
import com.econoMe.gestorgastosback.dto.OperationsDto;
import com.econoMe.gestorgastosback.model.*;
import com.econoMe.gestorgastosback.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/accounting")
public class AccountingController {

    @Autowired
    private AccountingService accountingService;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private UserService userService;
    @Autowired
    private OperationsService operationsService;
    @Autowired
    private MappingService mappingService;

    // Registro de contabilidad
    @PostMapping("/register")
    public ResponseEntity<AccountingDto> registerAccounting(@RequestBody AccountingRegistration accountingRegistration) {
        try {
            // Mapear la solicitud de registro a una entidad Accounting
            Accounting accounting = mappingService.accountingRegisterToAccounting(accountingRegistration);
            // Crear la contabilidad utilizando el servicio
            Accounting savedAccounting = accountingService.createAccounting(accounting);
            // Retornar la respuesta con la contabilidad creada
            return ResponseEntity.status(HttpStatus.CREATED).body(mappingService.accountingToDto(savedAccounting));
        } catch (Exception e) {
            // Si hay un error, retornar una respuesta con el código de error correspondiente
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Actualización de datos de la contabilidad
    @PutMapping("/{id}")
    public ResponseEntity<Accounting> updateAccounting(@PathVariable String username, @RequestBody Accounting accounting) {
        Accounting updatedAccounting = accountingService.updateAccounting(username, accounting);
        return ResponseEntity.ok(updatedAccounting);
    }

    @GetMapping("/accountingUserShared")
    public ResponseEntity<?> getUserById(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        String username = userDetails.getUsername();

        List<Accounting> accounting = accountingService.findAccountingsSharedByUser(userService.getUserByUsername(username));
        return ResponseEntity.ok(mappingService.accountingDtoList(accounting));
    }

    @GetMapping("/accountingPersonal")
    public ResponseEntity<?> getPersonalAccounting(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        Optional<Accounting> personalAccountingOptional = accountingService.findPersonalAccounting(userService.getUserByUsername(userDetails.getUsername()));
        if (personalAccountingOptional.isPresent()) {
            Accounting personalAccounting = personalAccountingOptional.get();
            return ResponseEntity.ok(mappingService.accountingToDto(personalAccounting));
        } else {
            // Manejo de caso donde no se encuentra la contabilidad personal
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/accountingUser")
    public ResponseEntity<?> getAllUserAccounting(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        Optional<Accounting> personalAccountingOptional = accountingService.findPersonalAccounting(userService.getUserByUsername(userDetails.getUsername()));
        if (personalAccountingOptional.isPresent()) {
            Accounting personalAccounting = personalAccountingOptional.get();
            List<Accounting> accountings = accountingService.findAccountingsSharedByUser(userService.getUserByUsername(userDetails.getUsername()));
            accountings.add(personalAccounting);

            return ResponseEntity.ok(mappingService.accountingDtoList(accountings));
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/{id}/rol")
    public ResponseEntity<?> getAccountingRole(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        Roles roles = rolesService.findRoleById(new RolesId(user.getId(), accountingService.findAccountingById(id).getId()))
                .orElseThrow(() -> new NoSuchElementException("No se encontró el rol para la contabilidad."));
        return ResponseEntity.ok(mappingService.rolesToDto(roles));
    }

    @GetMapping("/{id}/categories")
    public ResponseEntity<?> getAccountingOperationCategories(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(operationsService.findAllAccountingCategories(accountingService.findAccountingById(id)));
    }

    @PostMapping("/operation/register")
    public ResponseEntity<?> registerOperation(@RequestBody OperationsDto operationsDto) {
        try {
            // Mapear la solicitud de registro a una entidad Accounting
            Operations operation = mappingService.dtoToOperation(operationsDto);
            // Crear la contabilidad utilizando el servicio
            Operations savedOperation = operationsService.createOperation(operation);
            // Retornar la respuesta con la contabilidad creada
            return ResponseEntity.status(HttpStatus.CREATED).body(mappingService.operationsToDto(savedOperation));
        } catch (Exception e) {
            // Si hay un error, retornar una respuesta con el código de error correspondiente
            System.out.println("ERROOOOOOR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/operation/all")
    public ResponseEntity<?> getAllUserOperation(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findAllUserOperation(userService.getUserByUsername(userDetails.getUsername()))));
    }


    @GetMapping("/all")
    public ResponseEntity<List<Accounting>> getAllAccounting() {
        List<Accounting> accountings = accountingService.findAllAccounting();
        return ResponseEntity.ok(accountings);
    }


    // Ejemplo: Eliminar contabilidad
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccounting(@PathVariable Long id, @RequestBody User user) {
        accountingService.deleteAccounting(id, user);
        return ResponseEntity.ok().build();
    }
}
