package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.common.OperationType;
import com.econoMe.gestorgastosback.dto.*;
import com.econoMe.gestorgastosback.model.*;
import com.econoMe.gestorgastosback.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounting")
public class AccountingController {

    private final AccountingService accountingService;
    private final RolesService rolesService;
    private final UserService userService;
    private final OperationsService operationsService;
    private final MappingService mappingService;
    private final JwtService jwtService;

    @Autowired
    AccountingController(AccountingService accountingService, RolesService rolesService, UserService userService, OperationsService operationsService, MappingService mappingService, JwtService jwtService){
        this.accountingService = accountingService;
        this.rolesService = rolesService;
        this.userService = userService;
        this.operationsService = operationsService;
        this.mappingService = mappingService;
        this.jwtService = jwtService;
    }

    private User getUserFromRequest(HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        return jwtService.getUserFromToken(token);
    }

    // Registro de contabilidad
    @PostMapping("/register")
    public ResponseEntity<AccountingDto> registerAccounting(@RequestBody AccountingRegistration accountingRegistration) {
        Accounting savedAccounting = accountingService.createAccounting(mappingService.accountingRegisterToAccounting(accountingRegistration));
        return ResponseEntity.status(HttpStatus.CREATED).body(mappingService.accountingToDto(savedAccounting));
    }

    // Actualización de datos de la contabilidad
    @PutMapping("/{id}")
    public ResponseEntity<Accounting> updateAccounting(
            @PathVariable Long id,
            @RequestBody AccountingRegistration accountingRegistration) {

        Accounting updatedAccounting = accountingService.updateAccounting(id, userService.getUserByUsername(accountingRegistration.getUserCreator()), accountingRegistration.getName(), accountingRegistration.getDescription());
        return ResponseEntity.ok(updatedAccounting);
    }

    @GetMapping("/accountingUserShared")
    public ResponseEntity<?> findUserById(HttpServletRequest request) {

        List<Accounting> accounting = accountingService.findAccountingsSharedByUser(getUserFromRequest(request));
        return ResponseEntity.ok(mappingService.accountingDtoList(accounting));
    }

    @GetMapping("/accountingPersonal")
    public ResponseEntity<?> findPersonalAccounting(HttpServletRequest request) {

        Optional<Accounting> personalAccountingOptional = accountingService.findPersonalAccounting(getUserFromRequest(request));
        if (personalAccountingOptional.isPresent()) {
            Accounting personalAccounting = personalAccountingOptional.get();
            return ResponseEntity.ok(mappingService.accountingToDto(personalAccounting));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/accountingUser")
    public ResponseEntity<List<AccountingDto>> getAllUserAccounting(HttpServletRequest request) {

        List<Accounting> accountings = accountingService.findAllUserAccounting(getUserFromRequest(request));
        return ResponseEntity.ok(mappingService.accountingDtoList(accountings));
    }

    @GetMapping("/{id}/rol")
    public ResponseEntity<?> getAccountingRole(HttpServletRequest request, @PathVariable Long id) {

        Roles roles = rolesService.findById(new RolesId(getUserFromRequest(request).getId(), accountingService.findAccountingById(id).getId()));
        return ResponseEntity.ok(mappingService.rolesToDto(roles));
    }

    @GetMapping("/{id}/userCreator")
    public ResponseEntity<?> getAccountingUserCreator(@PathVariable Long id) {

        return ResponseEntity.ok(mappingService.accountingToDto(accountingService.findAccountingById(id)));
    }

    @GetMapping("/{id}/categoriesSpent")
    public ResponseEntity<?> getAccountingOperationCategoriesSpent(@PathVariable Long id) {

        return ResponseEntity.ok(operationsService.findAllAccountingCategoriesByType(accountingService.findAccountingById(id), OperationType.SPENT));
    }

    @GetMapping("/{id}/categoriesIncome")
    public ResponseEntity<?> getAccountingOperationCategoriesIncome(@PathVariable Long id) {

        return ResponseEntity.ok(operationsService.findAllAccountingCategoriesByType(accountingService.findAccountingById(id), OperationType.INCOME));
    }

    @PostMapping("/operation/register")
    public ResponseEntity<?> registerOperation(@RequestBody OperationsDto operationsDto) {
        // Mapear la solicitud de registro a una entidad Accounting
        Operations operation = mappingService.dtoToOperation(operationsDto);
        // Crear la contabilidad utilizando el servicio
        Operations savedOperation = operationsService.createOperation(operation);
        // Retornar la respuesta con la contabilidad creada
        return ResponseEntity.status(HttpStatus.CREATED).body(mappingService.operationsToDto(savedOperation));
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
        return ResponseEntity.ok(mappingService.operationsToDto(savedOperation));
    }

    @GetMapping("/{id}/operation/all")
    public ResponseEntity<?> getAllUserOperationByAccounting(HttpServletRequest request, @PathVariable Long id, @RequestParam(required = false) String filterType,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return getFilteredOperations(id, filterType, getUserFromRequest(request), startDate, endDate, null);
    }

    @GetMapping("/operation/all")
    public ResponseEntity<?> getAllUserOperation(HttpServletRequest request) {

        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findAllUserOperation(getUserFromRequest(request))));
    }

    @GetMapping("/operationAccountings/all")
    public ResponseEntity<?> getAllAccountingUserOperation(HttpServletRequest request) {

        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findAllOperationsAccountingUser(getUserFromRequest(request))));
    }

    private ResponseEntity<?> getFilteredOperations(Long id, String filterType, User user, LocalDate startDate, LocalDate endDate, OperationType operationType) {
        LocalDate start;
        LocalDate end;

        switch (filterType) {
            case "last7days":
                start = LocalDate.now().minus(7, ChronoUnit.DAYS);
                end = LocalDate.now();
                break;
            case "last30days":
                start = LocalDate.now().minus(30, ChronoUnit.DAYS);
                end = LocalDate.now();
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    return ResponseEntity.badRequest().body("Custom date range requires startDate and endDate");
                }
                start = startDate;
                end = endDate;
                break;
            default:
                // Por defecto, usa el mes actual como rango
                start = YearMonth.now().atDay(1);
                end = YearMonth.now().atEndOfMonth();
        }

        return ResponseEntity.ok(mappingService.operationListToDto(
                operationsService.findOperationsForDateRange(accountingService.findAccountingById(id),
                        operationType,
                        user,
                        start,
                        end)));
    }

    @GetMapping("/{id}/operation/spent")
    public ResponseEntity<?> getAccountingSpent(@PathVariable Long id) {

        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findByAccountingAndType(accountingService.findAccountingById(id), OperationType.SPENT)));
    }

    @GetMapping("/{id}/operation/spentFiltered")
    public ResponseEntity<?> getAccountingSpentFiltered(
            @PathVariable Long id,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return getFilteredOperations(id, filterType, null, startDate, endDate, OperationType.SPENT);
    }

    @GetMapping("/{id}/operation/income")
    public ResponseEntity<?> getAccountingIncome(@PathVariable Long id) {

        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findByAccountingAndType(accountingService.findAccountingById(id), OperationType.INCOME)));
    }

    @GetMapping("/{id}/operation/incomeFiltered")
    public ResponseEntity<?> getAccountingIncomeFiltered(
            @PathVariable Long id,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return getFilteredOperations(id, filterType, null, startDate, endDate, OperationType.INCOME);
    }


    @GetMapping("/{id}/operation/incomeMonth")
    public ResponseEntity<?> getAccountingIncomeMonth(@PathVariable Long id) {

        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findOperationsForDateRange(accountingService.findAccountingById(id), OperationType.INCOME, null, YearMonth.now().atDay(1), YearMonth.now().atEndOfMonth())));
    }

    @GetMapping("/{id}/categoryDifferences")
    public ResponseEntity<?> getCategoryDifferences(@PathVariable Long id, @RequestParam(required = false) String filterType,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Accounting accounting = accountingService.findAccountingById(id);

            LocalDate start;
            LocalDate end;

            switch (filterType) {
                case "last7days":
                    start = LocalDate.now().minus(7, ChronoUnit.DAYS);
                    end = LocalDate.now();
                    break;
                case "last30days":
                    start = LocalDate.now().minus(30, ChronoUnit.DAYS);
                    end = LocalDate.now();
                    break;
                case "custom":
                    if (startDate == null || endDate == null) {
                        return ResponseEntity.badRequest().body("Custom date range requires startDate and endDate");
                    }
                    start = startDate;
                    end = endDate;
                    break;
                default:
                    // Por defecto, usa el mes actual como rango
                    start = YearMonth.now().atDay(1);
                    end = YearMonth.now().atEndOfMonth();
            }

            Map<String, Double> categoryDifferencesSpent = operationsService.calculateSignificantCategoryDifferences(accounting, OperationType.SPENT, null, start, end);
            Map<String, Double> categoryDifferencesIncome = operationsService.calculateSignificantCategoryDifferences(accounting, OperationType.INCOME, null, start, end);

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
    public ResponseEntity<?> addUser(@PathVariable Long id, @RequestBody RolesDto rolesDto) {
        // Intenta obtener el usuario por ID de manera segura
        User userToAdd = userService.getUserByUsername(rolesDto.getUsername());

        // Intenta obtener la contabilidad por ID de manera segura
        Accounting accounting = accountingService.findAccountingById(id);

        // Crea el nuevo rol
        Roles newRole = new Roles(userToAdd, accounting, rolesDto.getRole());
        Roles createdRole = rolesService.createRole(newRole);

        // Retorna la respuesta con el rol creado
        return ResponseEntity.ok(mappingService.rolesToDto(createdRole));
    }


    @GetMapping("/{id}/users")
    public ResponseEntity<?> getAccountingUsers(@PathVariable Long id) {

        return ResponseEntity.ok(mappingService.rolesListToDto(rolesService.findAllAccountingUsersNotCreator(accountingService.findAccountingById(id))));
    }

    @DeleteMapping("/{id}/deleteUser")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestBody Map<String, String> body) {

        String username = body.get("username");
        User user = userService.getUserByUsername(username);
        rolesService.deleteRole(new RolesId(user.getId(), id));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/operation/filterOperation")
    public ResponseEntity<?> getFilteredOperations(HttpServletRequest request, @ModelAttribute OperationFilterDto operationFilterDto) {

        List<OperationsDto> filteredOperations = mappingService.operationListToDto(operationsService.findFilteredOperations(operationFilterDto, getUserFromRequest(request)));
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
