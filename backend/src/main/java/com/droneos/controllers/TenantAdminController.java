package com.droneos.controllers;

import com.droneos.dto.TenantOnboardingRequest;
import com.droneos.entities.TenantRegistry;
import com.droneos.services.TenantManagementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/tenants")
public class TenantAdminController {

    private final TenantManagementService tenantManagementService;

    public TenantAdminController(TenantManagementService tenantManagementService) {
        this.tenantManagementService = tenantManagementService;
    }

    @PostMapping("/onboard")
    public ResponseEntity<TenantRegistry> onboardTenant(@RequestBody TenantOnboardingRequest request) {

        TenantRegistry newTenant = tenantManagementService.onboardNewTenant(
                request.getSchoolName(),
                request.getSubdomain(),
                request.getDbSchemaName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(newTenant);
    }
}