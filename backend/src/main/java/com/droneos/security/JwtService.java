package com.droneos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * Architect's Note:
 * Handles token generation, cryptographic validation, and claim extraction
 * to securely retrieve multi-tenant context without trusting client headers.
 */
@Service
public class JwtService {

    // In production, move this secret key to application.yml / environment variables
    private static final String SECRET_KEY_STRING = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extracts all claims from a cryptographically signed token.
     * If the token has been tampered with, this will throw an exception.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Securely extracts the tenant database schema name from the verified JWT claims.
     */
    public String extractTenantSchema(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("tenantSchema", String.class);
    }
}