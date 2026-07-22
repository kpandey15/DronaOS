package com.droneos.repositories;

import com.droneos.entities.TenantRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRegistryRepository extends JpaRepository<TenantRegistry, UUID> {

    // Critical for the routing engine: Look up a school's details using their subdomain
    Optional<TenantRegistry> findBySubdomain(String subdomain);

    // Safety checks for the onboarding flow to prevent duplicate registrations
    boolean existsBySubdomain(String subdomain);

    boolean existsByDbSchemaName(String dbSchemaName);
}