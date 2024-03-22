package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.model.Operations;
import com.econoMe.gestorgastosback.service.OperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/operations")
public class OperationsController {

    @Autowired
    private OperationsService operationsService;

    @PostMapping("/register")
    public ResponseEntity<Operations> registerAccounting(@RequestBody Operations accounting) {// Codificamos la contraseña antes de guardarla
        Operations savedOperation = operationsService.createOperation(accounting);
        return ResponseEntity.ok(savedOperation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Operations> updateAccounting(@PathVariable Long id, @RequestBody Operations accounting) {
        Operations updatedOperation = operationsService.updateOperation(id, accounting);
        return ResponseEntity.ok(updatedOperation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Operations> getUserById(@PathVariable Long id) {
        Operations operation = operationsService.findById(id);
        return ResponseEntity.ok(operation);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Operations>> getAllAccounting() {
        List<Operations> operations = operationsService.findAllOperations();
        return ResponseEntity.ok(operations);
    }


    // Ejemplo: Eliminar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccounting(@PathVariable Long id) {
        operationsService.deleteOperation(id);
        return ResponseEntity.ok().build();
    }
}