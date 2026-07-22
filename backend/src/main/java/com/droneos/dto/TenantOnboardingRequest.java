package com.droneos.dto;

import lombok.Data;

@Data
public class TenantOnboardingRequest {
    private String schoolName;
    private String subdomain;
    private String dbSchemaName;
}