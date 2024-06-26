package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.common.OperationType;
import com.econoMe.gestorgastosback.common.Role;
import com.econoMe.gestorgastosback.dto.OperationFilterDto;
import com.econoMe.gestorgastosback.exception.OperationException;
import com.econoMe.gestorgastosback.exception.RoleException;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.repository.OperationsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OperationsService {

    private final OperationsRepository operationsRepository;
    private final RolesService rolesService;

    @Autowired
    public OperationsService(OperationsRepository operationsRepository, RolesService rolesService) {
        this.operationsRepository = operationsRepository;
        this.rolesService = rolesService;
    }

    // Crear una nueva operación
    public Operations createOperation(Operations operation) {
        // Verificar si el usuario tiene permisos de editor en la contabilidad especificada
        if (rolesService.findByUserUsernameAndAccountingId(operation.getUser().getUsername(), operation.getAccounting().getId()).getRole().equals(Role.EDITOR)) {
            return operationsRepository.save(operation);
        } else {
            throw new RoleException("El usuario " + operation.getUser().getUsername() + " no tiene rol de editor en la contabilidad " + operation.getAccounting().getId());
        }
    }

    // Crear múltiples operaciones
    public List<Operations> createOperations(List<Operations> operations) {
        return operations.stream().map(this::createOperation).collect(Collectors.toList());
    }

    // Encontrar todas las operaciones de todas las contabilidades a las que tiene acceso un usuario
    public List<Operations> findAllOperationsAccountingUser(User user) {
        return rolesService.findAllByUser(user).stream()
                .map(Roles::getAccounting)
                .flatMap(accounting -> operationsRepository.findByAccounting(accounting).stream())
                .collect(Collectors.toList());
    }

    // Encontrar operaciones por contabilidad
    public List<Operations> findByAccounting(Accounting accounting) {
        return operationsRepository.findByAccounting(accounting);
    }

    // Encontrar categorías de operaciones por tipo y contabilidad
    public List<String> findAllAccountingCategoriesByType(Accounting accounting, OperationType type) {
        return findByAccounting(accounting).stream()
                .filter(op -> op.getType() == type)
                .map(Operations::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    // Encontrar todas las operaciones de un usuario para una contabilidad específica
    public List<Operations> findAllUserOperationByAccounting(User user, Accounting accounting) {
        return operationsRepository.findByUserAndAccounting(user, accounting);
    }

    // Encontrar todas las operaciones de un usuario
    public List<Operations> findAllUserOperation(User user) {
        return operationsRepository.findByUser(user);
    }

    // Encontrar operaciones por contabilidad y tipo
    public List<Operations> findByAccountingAndType(Accounting accounting, OperationType type) {
        return operationsRepository.findByAccountingAndType(accounting, type);
    }

    // Encontrar operaciones en un rango de fechas específico
    public List<Operations> findOperationsForDateRange(Accounting accounting, OperationType type, User user, LocalDate start, LocalDate end) {
        return findByAccounting(accounting).stream()
                .filter(op -> (op.getDate().isEqual(start) || op.getDate().isAfter(start)) &&
                        (op.getDate().isEqual(end) || op.getDate().isBefore(end)) &&
                        (type == null || Objects.equals(type, op.getType())))
                .sorted((o1, o2) -> o2.getDate().compareTo(o1.getDate())) // Ordenar por fecha descendente
                .collect(Collectors.toList());
    }

    // Calcular diferencias significativas de categoría entre períodos
    public Map<String, Double> calculateSignificantCategoryDifferences(Accounting accounting, OperationType type, User user, LocalDate startDate, LocalDate endDate) {
        // Obtener las operaciones para el período actual y el período anterior
        List<Operations> operationsCurrentPeriod = findOperationsForDateRange(accounting, type, user, startDate, endDate);
        LocalDate startOfLastPeriod = startDate.minusMonths(1);
        LocalDate endOfLastPeriod = endDate.minusMonths(1).withDayOfMonth(endDate.minusMonths(1).lengthOfMonth());
        List<Operations> operationsLastPeriod = findOperationsForDateRange(accounting, type, user, startOfLastPeriod, endOfLastPeriod);

        // Agrupar y sumar cantidades por categoría para ambos períodos
        Map<String, Double> currentPeriodSumByCategory = operationsCurrentPeriod.stream()
                .collect(Collectors.groupingBy(Operations::getCategory, Collectors.summingDouble(Operations::getQuantity)));
        Map<String, Double> lastPeriodSumByCategory = operationsLastPeriod.stream()
                .collect(Collectors.groupingBy(Operations::getCategory, Collectors.summingDouble(Operations::getQuantity)));

        // Calcular diferencias por categoría
        Map<String, Double> categoryDifferences = new HashMap<>();
        currentPeriodSumByCategory.forEach((category, sum) -> {
            double lastPeriodSum = lastPeriodSumByCategory.getOrDefault(category, 0.0);
            double difference = sum - lastPeriodSum;
            categoryDifferences.put(category, difference);
        });

        // Separar en diferencias positivas y negativas, y calcular las máximas
        Map<String, Double> positiveDifferences = new HashMap<>();
        Map<String, Double> negativeDifferences = new HashMap<>();

        categoryDifferences.forEach((category, difference) -> {
            if (difference > 0) {
                positiveDifferences.put(category, difference);
            } else if (difference < 0) {
                negativeDifferences.put(category, difference);
            }
        });

        // Encontrar la máxima diferencia positiva y negativa
        double maxPositiveDifference = positiveDifferences.values().stream()
                .max(Double::compare)
                .orElse(0.0);

        double maxNegativeDifference = negativeDifferences.values().stream()
                .min(Double::compare)
                .orElse(0.0);

        // Encontrar la categoría correspondiente a las máximas diferencias
        String maxPositiveCategory = positiveDifferences.entrySet().stream()
                .filter(entry -> entry.getValue() == maxPositiveDifference)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("No hay diferencias");

        String maxNegativeCategory = negativeDifferences.entrySet().stream()
                .filter(entry -> entry.getValue() == maxNegativeDifference)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("No hay diferencias");

        Map<String, Double> result = new LinkedHashMap<>();
        result.put(maxPositiveCategory, maxPositiveDifference);
        result.put(maxNegativeCategory, maxNegativeDifference);

        return result;
    }

    // Filtrar operaciones basadas en criterios específicos
    public List<Operations> findFilteredOperations(OperationFilterDto operationFilterDto, User user) {
        // Obtener todas las operaciones para el usuario dado
        List<Operations> operations = findAllOperationsAccountingUser(user);

        // Filtrar por ID de contabilidad si está presente
        if (operationFilterDto.getAccountingId() != null) {
            operations = operations.stream()
                    .filter(op -> op.getAccounting().getId().equals(Long.valueOf(operationFilterDto.getAccountingId())))
                    .collect(Collectors.toList());
        }

        // Filtrar por tipo de operación si está presente
        if (operationFilterDto.getType() != null) {
            if (!operationFilterDto.getType().equalsIgnoreCase("BOTH")) {
                operations = operations.stream()
                        .filter(op -> op.getType().toString().equalsIgnoreCase(operationFilterDto.getType()))
                        .collect(Collectors.toList());
            }
        }

        // Filtrar por categoría si está presente
        if (operationFilterDto.getCategory() != null) {
            if(!operationFilterDto.getCategory().equalsIgnoreCase("ALL")){
                operations = operations.stream()
                        .filter(op -> op.getCategory().equalsIgnoreCase(operationFilterDto.getCategory()))
                        .collect(Collectors.toList());
            }
        }

        // Filtrar por cantidad mínima si está presente
        if (operationFilterDto.getQuantityMin() != null) {
            operations = operations.stream()
                    .filter(op -> op.getQuantity() >= operationFilterDto.getQuantityMin())
                    .collect(Collectors.toList());
        }

        // Filtrar por cantidad máxima si está presente
        if (operationFilterDto.getQuantityMax() != null) {
            operations = operations.stream()
                    .filter(op -> op.getQuantity() <= operationFilterDto.getQuantityMax())
                    .collect(Collectors.toList());
        }

        // Filtrar por fecha de inicio si está presente
        if (operationFilterDto.getDateStart() != null) {
            operations = operations.stream()
                    .filter(op -> !op.getDate().isBefore(operationFilterDto.getDateStart()))
                    .collect(Collectors.toList());
        }

        // Filtrar por fecha de fin si está presente
        if (operationFilterDto.getDateEnd() != null) {
            operations = operations.stream()
                    .filter(op -> !op.getDate().isAfter(operationFilterDto.getDateEnd()))
                    .collect(Collectors.toList());
        }

        // Ordenar las operaciones por fecha
        operations.sort(Comparator.comparing(Operations::getDate));

        return operations;
    }

    // Actualizar una operación existente
    public Operations updateOperation(Long id, Operations updatedOperation) {
        // Verificar si la operación existe y luego actualizarla
        if (id == null || !operationsRepository.existsById(id)) {
            throw new OperationException("La operación con el ID " + updatedOperation.getId() + " no existe.");
        }

        Operations existingOperation = operationsRepository.findById(id)
                .orElseThrow(() -> new OperationException("La operación con el ID " + id + " no fue encontrada."));

        // Actualizar sólo los campos que se supone deben cambiar
        existingOperation.setDescription(updatedOperation.getDescription());
        existingOperation.setQuantity(updatedOperation.getQuantity());
        existingOperation.setCategory(updatedOperation.getCategory());
        existingOperation.setDate(updatedOperation.getDate());
        existingOperation.setType(updatedOperation.getType());

        return operationsRepository.save(existingOperation);
    }

    // Eliminar una operación por su ID
    public void deleteOperation(Long id) {
        // Verificar si la operación existe y luego eliminarla
        if (!operationsRepository.existsById(id)) {
            throw new OperationException("La operación con el ID " + id + " no existe.");
        }
        operationsRepository.deleteById(id);
    }

    // Eliminar operaciones por contabilidad
    public void deleteByAccounting(Accounting accounting) {
        operationsRepository.deleteByAccounting(accounting);
    }

    // Eliminar operaciones por usuario
    public void deleteByUser(User user) {
        operationsRepository.deleteByUser(user);
    }

}

