package com.econoMe.gestorgastosback.controller;

import com.econoMe.gestorgastosback.model.Roles;
import com.econoMe.gestorgastosback.model.RolesId;
import com.econoMe.gestorgastosback.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RolesController {
    private final RolesService rolesService;

    @Autowired
    public RolesController(RolesService rolesService) {
        this.rolesService = rolesService;
    }

    @PostMapping("/register")
    public ResponseEntity<Roles> addRole(@RequestBody Roles role) {
        Roles newRole = rolesService.createRole(role);
        return new ResponseEntity<>(newRole, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}/{accountingId}")
    public ResponseEntity<Roles> updateRole(@PathVariable Long id, @PathVariable Long accountingId, @RequestBody Roles updatedRole) {
        RolesId rolesId = new RolesId(id, accountingId);
        return rolesService.updateRole(rolesId, updatedRole)
                .map(role -> new ResponseEntity<>(role, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{userId}/{accountingId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id, @PathVariable Long accountingId) {
        RolesId rolesId = new RolesId(id, accountingId);
        boolean deleted = rolesService.deleteRole(rolesId);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{userId}/{accountingId}")
    public ResponseEntity<Roles> findRoleById(@PathVariable Long id, @PathVariable Long accountingId) {
        RolesId rolesId = new RolesId(id, accountingId);
        return rolesService.findRoleById(rolesId)
                .map(role -> new ResponseEntity<>(role, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Roles>> findAllRoles() {
        List<Roles> roles = rolesService.findAllRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }
}
