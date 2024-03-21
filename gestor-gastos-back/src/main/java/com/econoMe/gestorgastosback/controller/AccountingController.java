package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.dto.AccountingDto;
import com.econoMe.gestorgastosback.model.Accounting;
import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.RolesId;
import com.econoMe.gestorgastosback.model.User;
import com.econoMe.gestorgastosback.service.AccountingService;
import com.econoMe.gestorgastosback.service.MappingService;
import com.econoMe.gestorgastosback.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/accounting")
public class AccountingController {

    @Autowired
    private AccountingService accountingService;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private MappingService mappingService;

    // Registro de contabilidad
    @PostMapping("/register")
    public ResponseEntity<AccountingDto> registerAccounting(@RequestBody AccountingDto accountingDto) {
        Accounting savedAccounting = accountingService.createAccounting(mappingService.accountingDtoToAccounting(accountingDto));
        return ResponseEntity.ok(accountingDto);
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

        List<Accounting> accounting = accountingService.findAccountingsSharedByUser(username);
        List<AccountingDto> accountingDtos = mappingService.accountingDtoList(accounting);
        return ResponseEntity.ok(accountingDtos);
    }

    @GetMapping("/accountingPersonal")
    public ResponseEntity<?> getPersonalAccounting(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        return ResponseEntity.ok(accountingService.findPersonalAccounting(userDetails.getUsername()));
    }

    @GetMapping("/{accountingName}/rol")
    public ResponseEntity<?> getAccountingRole(Authentication authentication, @PathVariable String accountingName) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No se ha proporcionado una autenticación válida.");
        }

        Roles roles = rolesService.findRoleById(new RolesId(userDetails.getUsername(), accountingService.findAccountingByName(userDetails.getUsername(), accountingName).getId()))
                .orElseThrow(() -> new NoSuchElementException("No se encontró el rol para la contabilidad."));
        return ResponseEntity.ok(mappingService.rolesToDto(roles));

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
