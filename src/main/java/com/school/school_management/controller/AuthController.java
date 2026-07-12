package com.school.school_management.controller;

import com.school.school_management.dto.response.AuthResponse;
import com.school.school_management.dto.request.LoginRequest;
import com.school.school_management.dto.request.RegisterRequest;
import com.school.school_management.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication",
        description = "Endpoints for registration and login"
)
public class AuthController {

   private final AuthService authService;

   public AuthController(AuthService authService) {
      this.authService = authService;
   }

   //POST /api/auth/register
   @PostMapping("/register")
   @ResponseStatus(HttpStatus.CREATED)
   @Operation(
           summary = "Register a new user",
           description = "Create a new user account with USER role"
   )
   @ApiResponses({
           @ApiResponse(
                   responseCode = "201",
                   description = "User registered successfully"
           ),
           @ApiResponse(
                   responseCode = "400",
                   description = "Validation failed or username/email taken"
           )
   })
   public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
         return authService.register(request);
   }

   //POST /api/auth/login
   @PostMapping("/login")
   @Operation(
           summary = "Login",
           description = "Authenticates user and returns JWT token"
   )
   @ApiResponses({
           @ApiResponse(
                   responseCode = "200",
                   description = "Login successful - JWT returned"
           ),
           @ApiResponse(
                   responseCode = "401",
                   description = "Invalid username or password"
           )
   })
   public AuthResponse login(@Valid @RequestBody LoginRequest request) {
      return authService.login(request);
   }
}
