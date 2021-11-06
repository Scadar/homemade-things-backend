package ru.homemadethings.homemadethings.auth.controller;

import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.homemadethings.homemadethings.auth.annotation.CurrentUser;
import ru.homemadethings.homemadethings.auth.model.CustomUserDetails;
import ru.homemadethings.homemadethings.auth.model.Role;
import ru.homemadethings.homemadethings.auth.service.RoleService;

import java.util.Collection;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Collection<Role>> loadAllRoles() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @PostMapping("/add-seller-role")
    public void addSellerRole(@CurrentUser CustomUserDetails user) throws NotFoundException {
        roleService.addSellerRoleToUser(user.getId());
    }
}
