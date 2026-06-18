package com.school.school_management.controller;

import com.school.school_management.dto.AuthResponse;
import com.school.school_management.dto.LoginRequest;
import com.school.school_management.dto.RegisterRequest;
import com.school.school_management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

   private final AuthService authService;

   public AuthController(AuthService authService) {
      this.authService = authService;
   }

   //POST /api/auth/register
   @PostMapping("/register")
   @ResponseStatus(HttpStatus.CREATED)
   public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
         return authService.register(request);
   }

   //POST /api/auth/login
   @PostMapping("/login")
   public AuthResponse login(@Valid @RequestBody LoginRequest request) {
      return authService.login(request);
   }
}
