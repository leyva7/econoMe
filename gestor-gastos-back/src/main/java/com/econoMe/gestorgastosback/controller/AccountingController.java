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

    // Obtiene el usuario de la solicitud HTTP mediante el token JWT
    private User getUserFromRequest(HttpServletRequest request) {
        String token = jwtService.getTokenFromRequest(request);
        return jwtService.getUserFromToken(token);
    }

    // Registro de nueva contabilidad
    @PostMapping("/register")
    public ResponseEntity<AccountingDto> registerAccounting(@RequestBody AccountingRegistration accountingRegistration) {
        Accounting savedAccounting = accountingService.createAccounting(mappingService.accountingRegisterToAccounting(accountingRegistration));
        return ResponseEntity.status(HttpStatus.CREATED).body(mappingService.accountingToDto(savedAccounting));
    }

    // Actualización de datos de una contabilidad existente
    @PutMapping("/{id}")
    public ResponseEntity<Accounting> updateAccounting(
            @PathVariable Long id,
            @RequestBody AccountingRegistration accountingRegistration) {

        Accounting updatedAccounting = accountingService.updateAccounting(id, userService.getUserByUsername(accountingRegistration.getUserCreator()), accountingRegistration.getName(), accountingRegistration.getDescription());
        return ResponseEntity.ok(updatedAccounting);
    }

    // Obtener contabilidades compartidas por el usuario
    @GetMapping("/accountingUserShared")
    public ResponseEntity<?> findUserById(HttpServletRequest request) {
        List<Accounting> accounting = accountingService.findAccountingsSharedByUser(getUserFromRequest(request));
        return ResponseEntity.ok(mappingService.accountingDtoList(accounting));
    }

    // Obtener contabilidad personal del usuario
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

    // Obtener todas las contabilidades del usuario
    @GetMapping("/accountingUser")
    public ResponseEntity<List<AccountingDto>> getAllUserAccounting(HttpServletRequest request) {
        List<Accounting> accountings = accountingService.findAllUserAccounting(getUserFromRequest(request));
        return ResponseEntity.ok(mappingService.accountingDtoList(accountings));
    }

    // Obtener rol del usuario en una contabilidad específica
    @GetMapping("/{id}/rol")
    public ResponseEntity<?> getAccountingRole(HttpServletRequest request, @PathVariable Long id) {
        Roles roles = rolesService.findById(new RolesId(getUserFromRequest(request).getId(), accountingService.findAccountingById(id).getId()));
        return ResponseEntity.ok(mappingService.rolesToDto(roles));
    }

    // Obtener usuario creador de una contabilidad específica
    @GetMapping("/{id}/userCreator")
    public ResponseEntity<?> getAccountingUserCreator(@PathVariable Long id) {
        return ResponseEntity.ok(mappingService.accountingToDto(accountingService.findAccountingById(id)));
    }

    // Obtener categorías de gasto de una contabilidad específica
    @GetMapping("/{id}/categoriesSpent")
    public ResponseEntity<?> getAccountingOperationCategoriesSpent(@PathVariable Long id) {
        return ResponseEntity.ok(operationsService.findAllAccountingCategoriesByType(accountingService.findAccountingById(id), OperationType.SPENT));
    }

    // Obtener categorías de ingreso de una contabilidad específica
    @GetMapping("/{id}/categoriesIncome")
    public ResponseEntity<?> getAccountingOperationCategoriesIncome(@PathVariable Long id) {
        return ResponseEntity.ok(operationsService.findAllAccountingCategoriesByType(accountingService.findAccountingById(id), OperationType.INCOME));
    }

    // Registro de una operación
    @PostMapping("/operation/register")
    public ResponseEntity<?> registerOperation(@RequestBody OperationsDto operationsDto) {
        Operations operation = mappingService.dtoToOperation(operationsDto);
        Operations savedOperation = operationsService.createOperation(operation);
        return ResponseEntity.status(HttpStatus.CREATED).body(mappingService.operationsToDto(savedOperation));
    }

    // Registro de múltiples operaciones
    @PostMapping("/operations/register")
    public ResponseEntity<?> registerOperations(@RequestBody List<OperationsDto> operationsDtos) {
        try {
            List<Operations> operations = operationsDtos.stream()
                    .map(mappingService::dtoToOperation)
                    .collect(Collectors.toList());
            List<Operations> savedOperations = operationsService.createOperations(operations);
            List<OperationsDto> savedOperationsDtos = savedOperations.stream()
                    .map(mappingService::operationsToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOperationsDtos);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Actualización de una operación existente
    @PutMapping("/operation")
    public ResponseEntity<OperationsDto> updateOperation(@RequestBody OperationsDto operationsDto) {
        Operations operation = mappingService.dtoToOperation(operationsDto);
        Operations savedOperation = operationsService.updateOperation(operationsDto.getId(), operation);
        return ResponseEntity.ok(mappingService.operationsToDto(savedOperation));
    }

    // Obtener todas las operaciones del usuario en una contabilidad específica con filtros opcionales
    @GetMapping("/{id}/operation/all")
    public ResponseEntity<?> getAllUserOperationByAccounting(HttpServletRequest request, @PathVariable Long id, @RequestParam(required = false) String filterType,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return getFilteredOperations(id, filterType, getUserFromRequest(request), startDate, endDate, null);
    }

    // Obtener todas las operaciones del usuario
    @GetMapping("/operation/all")
    public ResponseEntity<?> getAllUserOperation(HttpServletRequest request) {
        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findAllUserOperation(getUserFromRequest(request))));
    }

    // Obtener todas las operaciones contables del usuario
    @GetMapping("/operationAccountings/all")
    public ResponseEntity<?> getAllAccountingUserOperation(HttpServletRequest request) {
        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findAllOperationsAccountingUser(getUserFromRequest(request))));
    }

    // Obtener operaciones filtradas según parámetros
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

    // Obtener todas las operaciones de gasto de una contabilidad específica
    @GetMapping("/{id}/operation/spent")
    public ResponseEntity<?> getAccountingSpent(@PathVariable Long id) {
        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findByAccountingAndType(accountingService.findAccountingById(id), OperationType.SPENT)));
    }

    // Obtener operaciones de gasto filtradas de una contabilidad específica
    @GetMapping("/{id}/operation/spentFiltered")
    public ResponseEntity<?> getAccountingSpentFiltered(
            @PathVariable Long id,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return getFilteredOperations(id, filterType, null, startDate, endDate, OperationType.SPENT);
    }

    // Obtener todas las operaciones de ingreso de una contabilidad específica
    @GetMapping("/{id}/operation/income")
    public ResponseEntity<?> getAccountingIncome(@PathVariable Long id) {
        return ResponseEntity.ok(mappingService.operationListToDto(operationsService.findByAccountingAndType(accountingService.findAccountingById(id), OperationType.INCOME)));
    }

    // Obtener operaciones de ingreso filtradas de una contabilidad específica
    @GetMapping("/{id}/operation/incomeFiltered")
    public ResponseEntity<?> getAccountingIncomeFiltered(
            @PathVariable Long id,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return getFilteredOperations(id, filterType, null, startDate, endDate, OperationType.INCOME);
    }

    // Obtener diferencias significativas por categoría en un rango de fechas
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
                    start = YearMonth.now().atDay(1);
                    end = YearMonth.now().atEndOfMonth();
            }

            Map<String, Double> categoryDifferencesSpent = operationsService.calculateSignificantCategoryDifferences(accounting, OperationType.SPENT, null, start, end);
            Map<String, Double> categoryDifferencesIncome = operationsService.calculateSignificantCategoryDifferences(accounting, OperationType.INCOME, null, start, end);

            Map<String, Object> response = new HashMap<>();
            response.put("spentDifferences", categoryDifferencesSpent);
            response.put("incomeDifferences", categoryDifferencesIncome);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    // Añadir usuario a una contabilidad
    @PostMapping("/{id}/addUser")
    public ResponseEntity<?> addUser(@PathVariable Long id, @RequestBody RolesDto rolesDto) {
        User userToAdd = userService.getUserByUsername(rolesDto.getUsername());
        Accounting accounting = accountingService.findAccountingById(id);
        Roles newRole = new Roles(userToAdd, accounting, rolesDto.getRole());
        Roles createdRole = rolesService.createRole(newRole);
        return ResponseEntity.ok(mappingService.rolesToDto(createdRole));
    }

    // Obtener todos los usuarios de una contabilidad específica
    @GetMapping("/{id}/users")
    public ResponseEntity<?> getAccountingUsers(@PathVariable Long id) {
        return ResponseEntity.ok(mappingService.rolesListToDto(rolesService.findAllByAccounting(accountingService.findAccountingById(id))));
    }

    // Eliminar usuario de una contabilidad
    @DeleteMapping("/{id}/deleteUser")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String username = body.get("username");
        User user = userService.getUserByUsername(username);
        rolesService.deleteRole(new RolesId(user.getId(), id));
        return ResponseEntity.ok().build();
    }

    // Filtrar operaciones según parámetros
    @GetMapping("/operation/filterOperation")
    public ResponseEntity<?> getFilteredOperations(HttpServletRequest request, @ModelAttribute OperationFilterDto operationFilterDto) {
        List<OperationsDto> filteredOperations = mappingService.operationListToDto(operationsService.findFilteredOperations(operationFilterDto, getUserFromRequest(request)));
        return ResponseEntity.ok(filteredOperations);
    }

    // Eliminar una operación específica
    @DeleteMapping("operation/{id}")
    public ResponseEntity<?> deleteAccounting(@PathVariable Long id) {
        operationsService.deleteOperation(id);
        return ResponseEntity.ok().build();
    }

    // Eliminar contabilidad específica
    @DeleteMapping("/{id}/{username}")
    public ResponseEntity<?> deleteAccounting(@PathVariable Long id, @PathVariable String username) {
        accountingService.deleteAccounting(id, userService.getUserByUsername(username));
        return ResponseEntity.ok().build();
    }
}
