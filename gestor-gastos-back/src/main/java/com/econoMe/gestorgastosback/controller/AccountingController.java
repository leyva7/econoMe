package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.service.AccountingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounting")
public class AccountingController {

    @Autowired
    private AccountingService accountingService;

    // Registro de contabilidad
    @PostMapping("/register")
    public ResponseEntity<Accounting> registerAccounting(@RequestBody Accounting accounting) {// Codificamos la contraseña antes de guardarla
        Accounting savedAccounting = accountingService.createAccounting(accounting);
        return ResponseEntity.ok(savedAccounting);
    }

    // Actualización de datos de la contabilidad
    @PutMapping("/{id}")
    public ResponseEntity<Accounting> updateAccounting(@PathVariable String username, @RequestBody Accounting accounting) {
        Accounting updatedAccounting = accountingService.updateAccounting(username, accounting);
        return ResponseEntity.ok(updatedAccounting);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Accounting> getUserById(@PathVariable Long id) {
        Accounting accounting = accountingService.findAccountingById(id);
        return ResponseEntity.ok(accounting);
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
