package com.droneos.services;

import com.droneos.entities.TenantRegistry;
import com.droneos.repositories.TenantRegistryRepository; // Assume you created this standard JpaRepository
import org.springframework.stereotype.Service;

@Service
public class TenantManagementService {

    private final TenantRegistryRepository tenantRegistryRepository;
    private final TenantProvisioningService tenantProvisioningService;

    public TenantManagementService(TenantRegistryRepository tenantRegistryRepository,
                                   TenantProvisioningService tenantProvisioningService) {
        this.tenantRegistryRepository = tenantRegistryRepository;
        this.tenantProvisioningService = tenantProvisioningService;
    }

    public TenantRegistry onboardNewTenant(String schoolName, String subdomain, String dbSchemaName) {
        // 1. Save the routing metadata to the global registry
        TenantRegistry registry = new TenantRegistry();
        registry.setSchoolName(schoolName);
        registry.setSubdomain(subdomain);
        registry.setDbSchemaName(dbSchemaName);
        registry.setSubscriptionStatus("ACTIVE");

        TenantRegistry savedRegistry = tenantRegistryRepository.save(registry);

        // 2. Trigger the infrastructure build (Create schema + Run Flyway)
        tenantProvisioningService.provisionTenant(dbSchemaName);

        return savedRegistry;
    }
}