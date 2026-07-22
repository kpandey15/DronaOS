package com.droneos.services;

import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;

@Service
public class TenantProvisioningService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    public TenantProvisioningService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
    }

    /**
     * Creates a new isolated database schema and runs migration scripts to build tables.
     *
     * @param schemaName The unique schema name for the new school
     */
    public void provisionTenant(String schemaName) {
        // Step 1: Strictly validate the schema name to prevent SQL Injection
        validateSchemaName(schemaName);

        // Step 2: Safely execute the raw SQL command
        System.out.println("Building isolated container for: " + schemaName);
        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);

        // Step 3: Programmatically trigger Flyway in the new schema
        runFlywayMigrations(schemaName);
    }

    private void validateSchemaName(String schemaName) {
        // Only allow lowercase letters, numbers, and underscores. No spaces or special characters.
        if (schemaName == null || !schemaName.matches("^[a-z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid schema name: " + schemaName);
        }
    }

    private void runFlywayMigrations(String schemaName) {
        System.out.println("Deploying operational tables to: " + schemaName);

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName) // Lock Flyway exclusively to this new schema
                .locations("classpath:db/migration/tenant") // The folder containing your operational SQL scripts
                .load();

        flyway.migrate();
        System.out.println("Deployment successful for: " + schemaName);
    }
}