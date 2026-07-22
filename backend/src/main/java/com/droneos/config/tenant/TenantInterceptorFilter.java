package com.droneos.config.tenant;

import com.droneos.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Architect's Note:
 * Secure Tenant Interceptor. Extracts and cryptographically validates
 * the tenant schema name directly from the verified JWT bearer token.
 */
@Component
public class TenantInterceptorFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    public TenantInterceptorFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        String tenantId = null;

        try {
            // 1. Check if the request contains a valid Bearer token structure
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                String token = authHeader.substring(BEARER_PREFIX.length());

                try {
                    // 2. Cryptographically verify token and extract schema claim safely
                    tenantId = jwtService.extractTenantSchema(token);
                } catch (Exception e) {
                    // Log signature failure or malformed token
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token.");
                    return;
                }
            }

            // 3. Bind the extracted tenant to the thread-safe vault
            if (tenantId != null && !tenantId.trim().isEmpty()) {
                TenantContextHolder.setTenantId(tenantId);
            } else {
                // Fallback to public schema for unauthenticated endpoints (like login/register)
                TenantContextHolder.setTenantId(TenantContextHolder.DEFAULT_TENANT);
            }

            // 4. Proceed down the filter chain to controllers/services
            filterChain.doFilter(request, response);

        } finally {
            // 5. CRITICAL SAFEGUARD: Always clear thread-local vault to prevent data bleed
            TenantContextHolder.clear();
        }
    }
}