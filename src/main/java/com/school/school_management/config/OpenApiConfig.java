package com.school.school_management.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "School Management API",
                version = "1.0",
                description = "REST API for managing students, courses and teachers",
                contact = @Contact(
                        name = "Your Name",
                        email = "your@email.com"
                ),
                license = @License(
                        name = "MIT License"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Development Server"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        // ↑ Name we use to reference this security scheme
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter your JWT token here"
)
// ↑ This tells Swagger UI:
//   "There is a JWT security scheme!"
//   "Show a lock icon on protected endpoints!"
//   "Let users enter their token!"
public class OpenApiConfig {
   // Configuration is done through annotations above!
   // No code needed inside!
}
