package com.droneos.integration;

import com.droneos.security.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecureTenantJwtIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    // Must match the exact secret key used in JwtService
    private static final String SECRET_KEY_STRING = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        // Programmatically initialize MockMvc using the web application context
        // to bypass any missing test-autoconfigure classloader issues.
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Should successfully extract tenant schema from JWT, bind to thread vault, and route request")
    void testTenantExtractionAndRoutingViaJwt() throws Exception {
        // Arrange: Mint a valid signed JWT embedding our target school schema
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8));
        String targetSchema = "greenwood_schema";

        String signedToken = Jwts.builder()
                .subject("admin@greenwood.com")
                .claim("tenantSchema", targetSchema)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();

        // Act & Assert: Execute a mock HTTP request hitting an endpoint with the Bearer token
        mockMvc.perform(get("/api/tenants")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + signedToken))
                .andExpect(status().is2xxSuccessful());
    }
}