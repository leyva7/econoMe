package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.common.OperationType;
import com.econoMe.gestorgastosback.dto.*;
import com.econoMe.gestorgastosback.model.*;
import com.econoMe.gestorgastosback.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

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

    @GetMapping("/{id}/userCreator")
    public ResponseEntity<?> getAccountingUserCreator(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(mappingService.accountingToDto(accountingService.findAccountingById(id)));
    }

    @GetMapping("/{id}/categoriesSpent")
    public ResponseEntity<?> getAccountingOperationCategoriesSpent(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(operationsService.findAllAccountingCategoriesByType(accountingService.findAccountingById(id), OperationType.SPENT));
    }

    @GetMapping("/{id}/categoriesIncome")
    public ResponseEntity<?> getAccountingOperationCategoriesIncome(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(operationsService.findAllAccountingCategoriesByType(accountingService.findAccountingById(id), OperationType.INCOME));
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/operations/register")
    public ResponseEntity<?> registerOperations(@RequestBody List<OperationsDto> operationsDtos) {
        try {
            // Convertir la lista de DTOs a entidades de Operations
            List<Operations> operations = operationsDtos.stream()
                    .map(mappingService::dtoToOperation)
                    .collect(Collectors.toList());
            // Crear las operaciones utilizando el servicio
            List<Operations> savedOperations = operationsService.createOperations(operations);
            // Convertir las operaciones guardadas a DTOs
            List<OperationsDto> savedOperationsDtos = savedOperations.stream()
                    .map(mappingService::operationsToDto)
                    .collect(Collectors.toList());
            // Retornar la respuesta con las operaciones creadas
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOperationsDtos);
        } catch (Exception e) {
            // Si hay un error, retornar una respuesta con el código de error correspondiente
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/operation")
    public ResponseEntity<OperationsDto> updateOperation(@RequestBody OperationsDto operationsDto) {
        // Mapear la solicitud de registro a una entidad Accounting
        Operations operation = mappingService.dtoToOperation(operationsDto);
        // Crear la contabilidad utilizando el servicio
        Operations savedOperation = operationsService.updateOperation(operationsDto.getId(), operation);
        // Retornar la respuesta con la contabilidad creada
        return ResponseEntity.status(HttpStatus.CREATED).body(mappingService.operationsToDto(savedOperation));
    }

    @GetMapping("/{id}/operation/all")
    public ResponseEntity<?> getAllUserOperationByAccounting(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findAllUserOperationByAccounting(user, accountingService.findAccountingById(id))));
    }

    @GetMapping("/operation/all")
    public ResponseEntity<?> getAllUserOperation(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }
        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findAllUserOperation(user)));
    }

    @GetMapping("/operationAccountings/all")
    public ResponseEntity<?> getAllAccountingUserOperation(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }
        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findAllOperationsAccountingUser(user)));
    }

    @GetMapping("/{id}/operation/spent")
    public ResponseEntity<?> getAccountingSpent(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findByAccountingAndType(accountingService.findAccountingById(id), OperationType.SPENT)));
    }

    @GetMapping("/{id}/operation/spentMonth")
    public ResponseEntity<?> getAccountingSpentMonth(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findOperationsForMonth(accountingService.findAccountingById(id), OperationType.SPENT, YearMonth.now().atDay(1), YearMonth.now().atEndOfMonth())));
    }

    @GetMapping("/{id}/operation/income")
    public ResponseEntity<?> fetAccountingIncome(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findByAccountingAndType(accountingService.findAccountingById(id), OperationType.INCOME)));
    }

    @GetMapping("/{id}/operation/incomeMonth")
    public ResponseEntity<?> getAccountingIncomeMonth(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findOperationsForMonth(accountingService.findAccountingById(id), OperationType.INCOME, YearMonth.now().atDay(1), YearMonth.now().atEndOfMonth())));
    }

    @GetMapping("/{id}/categoryDifferences")
    public ResponseEntity<?> getCategoryDifferences(@PathVariable Long id) {
        try {
            Accounting accounting = accountingService.findAccountingById(id);
            if (accounting == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Accounting not found with ID: " + id);
            }

            // Suponiendo que tienes un método en operationsService que calcula las diferencias
            Map<String, Double> categoryDifferencesSpent = operationsService.calculateSignificantCategoryDifferences(accounting, OperationType.SPENT);
            Map<String, Double> categoryDifferencesIncome = operationsService.calculateSignificantCategoryDifferences(accounting, OperationType.INCOME);

            // Crear un objeto de respuesta que combine ambas listas de diferencias
            Map<String, Object> response = new HashMap<>();
            response.put("spentDifferences", categoryDifferencesSpent);
            response.put("incomeDifferences", categoryDifferencesIncome);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Manejar excepciones adecuadamente
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/addUser")
    public ResponseEntity<?> addUser(Authentication authentication, @PathVariable Long id, @RequestBody RolesDto rolesDto) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        // Intenta obtener el usuario por ID de manera segura
        User userToAdd = userService.getUserByUsername(rolesDto.getUsername());

        // Intenta obtener la contabilidad por ID de manera segura
        Accounting accounting = accountingService.findAccountingById(rolesDto.getAccountingId());

        // Crea el nuevo rol
        Roles newRole = new Roles(userToAdd, accounting, rolesDto.getRole());
        Roles createdRole = rolesService.createRole(newRole);

        // Retorna la respuesta con el rol creado
        return ResponseEntity.ok(mappingService.rolesToDto(createdRole));
    }


    @GetMapping("/{id}/users")
    public ResponseEntity<?> getAccountingUsers(Authentication authentication, @PathVariable Long id) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(mappingService.rolesListToDto(rolesService.findAllAccountingUsersNotCreator(accountingService.findAccountingById(id))));
    }

    @DeleteMapping("/{id}/deleteUser")
    public ResponseEntity<?> deleteUser(Authentication authentication, @PathVariable Long id, @RequestBody Map<String, String> body) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }
        String username = body.get("username");
        User user = userService.getUserByUsername(username);
        rolesService.deleteRole(new RolesId(user.getId(), id));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/operation/filterOperation")
    public ResponseEntity<?> getFilteredOperations(Authentication authentication, @ModelAttribute OperationFilterDto operationFilterDto) {
        // Usar operationFilterDto para filtrar las operaciones en el servicio y devolver los resultados
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }
        List<OperationsDto> filteredOperations = mappingService.operationListToDto(operationsService.findFilteredOperations(operationFilterDto, user));
        return ResponseEntity.ok(filteredOperations);
    }

    @DeleteMapping("operation/{id}")
    public ResponseEntity<?> deleteAccounting(@PathVariable Long id) {
        operationsService.deleteOperation(id);
        return ResponseEntity.ok().build();
    }

    // Ejemplo: Eliminar contabilidad
    @DeleteMapping("/{id}/{username}")
    public ResponseEntity<?> deleteAccounting(@PathVariable Long id, @PathVariable String username) {
        accountingService.deleteAccounting(id, userService.getUserByUsername(username));
        return ResponseEntity.ok().build();
    }
}
