-- Creates the master directory in the public schema
CREATE TABLE tenant_registry (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    school_name VARCHAR(255) NOT NULL,
    db_schema_name VARCHAR(255) UNIQUE NOT NULL,
    subdomain VARCHAR(255) UNIQUE NOT NULL,
    subscription_status VARCHAR(50) NOT NULL
);