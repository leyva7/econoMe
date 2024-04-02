package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.common.OperationType;
import com.econoMe.gestorgastosback.common.Role;
import com.econoMe.gestorgastosback.common.Type;
import com.econoMe.gestorgastosback.controller.AccountingController;
import com.econoMe.gestorgastosback.dto.OperationFilterDto;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.OperationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import java.time.ZoneId;

@Service
public class OperationsService {

    @Autowired
    private final OperationsRepository operationsRepository;
    @Autowired
    private final RolesService rolesService;

    @Autowired
    public OperationsService(OperationsRepository operationsRepository, RolesService rolesService) {
        this.operationsRepository = operationsRepository;
        this.rolesService = rolesService;
    }

    public List<Operations> findAllOperations() {
        return operationsRepository.findAll();
    }

    public List<Operations> findAllOperationsAccountingUser(User user) {
        // Obtener todos los roles del usuario
        List<Roles> roles = rolesService.findAllByUser(user);

        // Crear una lista para almacenar las operaciones de las contabilidades asociadas al usuario
        List<Operations> operationsAccountingUser = new ArrayList<>();

        // Iterar sobre los roles del usuario
        for (Roles role : roles) {
            // Obtener la contabilidad asociada al rol
            Accounting accounting = role.getAccounting();

            // Obtener las operaciones asociadas a la contabilidad
            List<Operations> operations = operationsRepository.findByAccounting(accounting);

            // Agregar las operaciones a la lista general
            operationsAccountingUser.addAll(operations);
        }

        return operationsAccountingUser;
    }

    public Operations findById(Long id) {
        return operationsRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No se encontró la operación de ID: " + id));
    }

    public List<Operations> findByAccounting(Accounting accounting) {
        List<Operations> operations = operationsRepository.findByAccounting(accounting);

        if (operations.isEmpty()) {
            return new ArrayList<>();
        } else {
            return operations;
        }
    }

    public List<Operations> findByUsername(User user) {
        List<Operations> operations = operationsRepository.findByUser(user);

        if (operations.isEmpty()) {
            throw new RuntimeException("La lista del usuario " + user.getUsername() + " está vacía");
        } else {
            return operations;
        }

    }

    public Operations createOperation(Operations operation) {

        if(rolesService.findByUserUsernameAndAccountingId(operation.getUser().getUsername(), operation.getAccounting().getId()).isEmpty()){
            throw new IllegalStateException("No existe ningún rol para el usuario " + operation.getUser().getUsername() + " y la contabilidad " + operation.getAccounting().getName()+ " no existe.");
        }

        Optional<Roles> rolesOptional = rolesService.findByUserUsernameAndAccountingId(operation.getUser().getUsername(), operation.getAccounting().getId());

        if (rolesOptional.isPresent()) {
            if(rolesOptional.get().getRole().equals(Role.EDITOR)){
                return operationsRepository.save(operation);
            }
            else{
                throw new RuntimeException("El usuario " + operation.getUser().getUsername() + " no tiene rol de editor en la contabilidad " + operation.getAccounting().getId());
            }

        } else {
            throw new RuntimeException("No se encontró la tupla para el usuario " + operation.getUser().getUsername() + " y la contabilidad " + operation.getAccounting().getId());
        }

    }

    public List<Operations> createOperations(List<Operations> operations) {

        List<Operations> savedOperations = new ArrayList<>();

        for(int i=0; i< operations.size(); i++){
            savedOperations.add(createOperation(operations.get(i)));
        }

        return savedOperations;

    }

    public List<String> findAllAccountingCategoriesByType(Accounting accounting, OperationType type) {
        List<Operations> operationsAccounting = findByAccounting(accounting);
        Set<String> uniqueCategories = new HashSet<>();

        for (Operations operation : operationsAccounting) {
            if (operation.getType().equals(type)) {
                uniqueCategories.add(operation.getCategory());
            }
        }

        return new ArrayList<>(uniqueCategories);
    }


    public List<Operations> findAllUserOperationByAccounting(User user, Accounting accounting) {
        return operationsRepository.findByUserAndAccounting(user, accounting);
    }

    public List<Operations> findAllUserOperation(User user) {
        return operationsRepository.findByUser(user);
    }

    public List<Operations> findByAccountingAndType(Accounting accounting, OperationType type) {
        return operationsRepository.findByAccountingAndType(accounting, type);
    }

    public List<Operations> findOperationsForMonth(Accounting accounting, OperationType type, LocalDate startOfMonth, LocalDate endOfMonth) {
        List<Operations> allOperations = findByAccountingAndType(accounting, type); // Obtiene todas las operaciones para la contabilidad

        return allOperations.stream()
                .filter(operation -> {
                    LocalDate operationDate = operation.getDate(); // Ya es LocalDate, no se necesita conversión
                    return (!operationDate.isBefore(startOfMonth) && !operationDate.isAfter(endOfMonth));
                })
                .collect(Collectors.toList());
    }

    public Map<String, Double> calculateSignificantCategoryDifferences(Accounting accounting, OperationType type) {
        // Obtener las operaciones del mes actual
        List<Operations> currentMonthOperations = findOperationsForMonth(accounting, type, YearMonth.now().atDay(1), YearMonth.now().atEndOfMonth());
        // Obtener las operaciones del mes anterior
        LocalDate startOfLastMonth = YearMonth.now().minusMonths(1).atDay(1);
        LocalDate endOfLastMonth = YearMonth.now().minusMonths(1).atEndOfMonth();
        List<Operations> lastMonthOperations = findOperationsForMonth(accounting, type, startOfLastMonth, endOfLastMonth);

        // Agrupar y sumar las cantidades por categoría para el mes actual
        Map<String, Double> currentMonthSumByCategory = currentMonthOperations.stream()
                .collect(Collectors.groupingBy(Operations::getCategory, Collectors.summingDouble(Operations::getQuantity)));

        // Agrupar y sumar las cantidades por categoría para el mes anterior
        Map<String, Double> lastMonthSumByCategory = lastMonthOperations.stream()
                .collect(Collectors.groupingBy(Operations::getCategory, Collectors.summingDouble(Operations::getQuantity)));

        // Calculando diferencias
        Map<String, Double> categoryDifferences = new HashMap<>();
        currentMonthSumByCategory.forEach((category, sum) -> {
            double lastMonthSum = lastMonthSumByCategory.getOrDefault(category, 0.0);
            double difference = sum - lastMonthSum;
            categoryDifferences.put(category, difference);
        });

        // Separar en positivos y negativos
        Map<String, Double> positiveDifferences = categoryDifferences.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, Double> negativeDifferences = categoryDifferences.entrySet().stream()
                .filter(entry -> entry.getValue() < 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Encontrar los valores más grandes positivos y negativos
        Optional<Map.Entry<String, Double>> maxPositive = positiveDifferences.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        Optional<Map.Entry<String, Double>> maxNegative = negativeDifferences.entrySet().stream()
                .min(Map.Entry.comparingByValue());

        Map<String, Double> result = new LinkedHashMap<>();
        maxPositive.ifPresent(entry -> result.put(entry.getKey(), entry.getValue()));
        maxNegative.ifPresent(entry -> result.put(entry.getKey(), entry.getValue()));

        return result;
    }

    public List<Operations> findFilteredOperations(OperationFilterDto operationFilterDto, User user) {
        // Obtener todas las operaciones para el usuario dado.
        List<Operations> operations = findAllOperationsAccountingUser(user);

        // Filtrar por ID de contabilidad si está presente.
        if (operationFilterDto.getAccountingId() != null) {
            operations = operations.stream()
                    .filter(op -> op.getAccounting().getId().equals(Long.valueOf(operationFilterDto.getAccountingId())))
                    .collect(Collectors.toList());
        }

        // Filtrar por tipo de operación si está presente.
        if (operationFilterDto.getType() != null) {
            if (!operationFilterDto.getType().equalsIgnoreCase("BOTH")) {
                operations = operations.stream()
                        .filter(op -> op.getType().toString().equalsIgnoreCase(operationFilterDto.getType()))
                        .collect(Collectors.toList());
            }
        }

        // Filtrar por categoría si está presente.
        if (operationFilterDto.getCategory() != null) {
            if(!operationFilterDto.getCategory().equalsIgnoreCase("ALL")){
                operations = operations.stream()
                        .filter(op -> op.getCategory().equalsIgnoreCase(operationFilterDto.getCategory()))
                        .collect(Collectors.toList());
            }
        }

        // Filtrar por cantidad mínima si está presente.
        if (operationFilterDto.getQuantityMin() != null) {
            operations = operations.stream()
                    .filter(op -> op.getQuantity() >= operationFilterDto.getQuantityMin())
                    .collect(Collectors.toList());
        }

        // Filtrar por cantidad máxima si está presente.
        if (operationFilterDto.getQuantityMax() != null) {
            operations = operations.stream()
                    .filter(op -> op.getQuantity() <= operationFilterDto.getQuantityMax())
                    .collect(Collectors.toList());
        }

        // Filtrar por fecha de inicio si está presente.
        if (operationFilterDto.getDateStart() != null) {
            operations = operations.stream()
                    .filter(op -> !op.getDate().isBefore(operationFilterDto.getDateStart()))
                    .collect(Collectors.toList());
        }

        // Filtrar por fecha de fin si está presente.
        if (operationFilterDto.getDateEnd() != null) {
            operations = operations.stream()
                    .filter(op -> !op.getDate().isAfter(operationFilterDto.getDateEnd()))
                    .collect(Collectors.toList());
        }

        Collections.sort(operations, Comparator.comparing(Operations::getDate));

        return operations;
    }

    public Operations updateOperation(Long id, Operations operation) {
        if (id == null || !operationsRepository.existsById(id)) {
            throw new IllegalStateException("La operación con el ID " + operation.getId() + " no existe.");
        }
        return operationsRepository.save(operation);
    }

    public void updateUserOperation(User oldUser, User newUser) {
        List<Operations> operation = findAllUserOperation(oldUser);
        List<Operations> newOperation = new ArrayList<>();
        for(int i = 0; i < operation.size(); i++){
            newOperation.add(operation.get(i));
            newOperation.get(i).setUser(newUser);
            createOperation(newOperation.get(i));
        }
    }

    public void deleteOperation(Long id) {
        if (!operationsRepository.existsById(id)) {
            throw new IllegalStateException("La operación con el ID " + id + " no existe.");
        }
        operationsRepository.deleteById(id);
    }

    public void deleteByAccounting(Accounting accounting) {
        operationsRepository.deleteByAccounting(accounting);
    }

    public void deleteByUser(User user) {
        if (!operationsRepository.existsByUser(user)) {
            throw new IllegalStateException("La operación con el usuario " + user.getUsername() + " no existe.");
        }
        operationsRepository.deleteByUser(user);
    }

}
