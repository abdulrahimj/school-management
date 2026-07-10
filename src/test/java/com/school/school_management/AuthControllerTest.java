package com.school.school_management;

import tools.jackson.databind.ObjectMapper;
import com.school.school_management.dto.request.LoginRequest;
import com.school.school_management.dto.request.RegisterRequest;
import com.school.school_management.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private ObjectMapper objectMapper;

   @Autowired
   private UserRepository userRepository;

   @BeforeEach
   void setUp() {
      userRepository.deleteAll();
   }

   // ─────────────────────────────────────────────
   // TEST: Register success
   // ─────────────────────────────────────────────
   @Test
   @DisplayName("Should register user successfully")
   void shouldRegisterUserSuccessfully() throws Exception {

      RegisterRequest request = new RegisterRequest();
      request.setUsername("alice");
      request.setEmail("alice@email.com");
      request.setPassword("password123");

      mockMvc.perform(
                      post("/api/auth/register")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request))
              )
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.token").isNotEmpty())
              // ↑ JWT token returned!
              .andExpect(jsonPath("$.username").value("alice"))
              .andExpect(jsonPath("$.role").value("USER"))
              .andExpect(jsonPath("$.message")
                      .value("Registration successful!"));
   }

   // ─────────────────────────────────────────────
   // TEST: Register duplicate username
   // ─────────────────────────────────────────────
   @Test
   @DisplayName("Should fail when username already exists")
   void shouldFailWhenUsernameExists() throws Exception {

      // Register first time
      RegisterRequest request = new RegisterRequest();
      request.setUsername("alice");
      request.setEmail("alice@email.com");
      request.setPassword("password123");

      mockMvc.perform(
                      post("/api/auth/register")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(request))
              )
              .andExpect(status().isCreated());

      // Register AGAIN with same username!
      RegisterRequest duplicate = new RegisterRequest();
      duplicate.setUsername("alice");
      duplicate.setEmail("alice2@email.com");
      duplicate.setPassword("password123");

      mockMvc.perform(
                      post("/api/auth/register")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(duplicate))
              )
              .andExpect(status().is4xxClientError());
      // ↑ Should fail! (We'll improve this in homework)
   }

   // ─────────────────────────────────────────────
   // TEST: Login success
   // ─────────────────────────────────────────────
   @Test
   @DisplayName("Should login successfully")
   void shouldLoginSuccessfully() throws Exception {

      // First register
      RegisterRequest register = new RegisterRequest();
      register.setUsername("bob");
      register.setEmail("bob@email.com");
      register.setPassword("password123");

      mockMvc.perform(
                      post("/api/auth/register")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(register))
              )
              .andExpect(status().isCreated());

      // Then login
      LoginRequest login = new LoginRequest();
      login.setUsername("bob");
      login.setPassword("password123");

      mockMvc.perform(
                      post("/api/auth/login")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(login))
              )
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.token").isNotEmpty())
              .andExpect(jsonPath("$.username").value("bob"))
              .andExpect(jsonPath("$.message")
                      .value("Login successful!"));
   }

   // ─────────────────────────────────────────────
   // TEST: Login with wrong password
   // ─────────────────────────────────────────────
   @Test
   @DisplayName("Should fail login with wrong password")
   void shouldFailLoginWithWrongPassword() throws Exception {

      // Register
      RegisterRequest register = new RegisterRequest();
      register.setUsername("charlie");
      register.setEmail("charlie@email.com");
      register.setPassword("correctpassword");

      mockMvc.perform(
                      post("/api/auth/register")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(register))
              )
              .andExpect(status().isCreated());

      // Login with WRONG password
      LoginRequest login = new LoginRequest();
      login.setUsername("charlie");
      login.setPassword("wrongpassword");

      mockMvc.perform(
                      post("/api/auth/login")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(login))
              )
              .andExpect(status().is4xxClientError());
      // ↑ Authentication fails!
   }
}
