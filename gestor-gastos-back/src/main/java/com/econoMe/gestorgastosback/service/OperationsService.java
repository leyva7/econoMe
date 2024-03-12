package com.econoMe.gestorgastosback.service;

import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.repository.OperationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OperationsService {

    private final OperationsRepository operationsRepository;

    @Autowired
    public OperationsService(OperationsRepository operationsRepository) {
        this.operationsRepository = operationsRepository;
    }

    public List<Operations> getAllOperations() {
        return operationsRepository.findAll();
    }

    public Operations getOperationById(Long id) {
        return operationsRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No se encontró la operación de ID: " + id));
    }

    public Operations createOperation(Operations operation) {
        return operationsRepository.save(operation);
    }

    public Operations updateOperation(Long id, Operations operation) {
        if (id == null || !operationsRepository.existsById(id)) {
            throw new IllegalStateException("La operación con el ID " + operation.getId() + " no existe.");
        }
        return operationsRepository.save(operation);
    }

    public void deleteOperation(Long id) {
        if (!operationsRepository.existsById(id)) {
            throw new IllegalStateException("La operación con el ID " + id + " no existe.");
        }
        operationsRepository.deleteById(id);
    }

}
