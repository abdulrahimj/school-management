package com.school.school_management.service;

import com.school.school_management.dto.AuthResponse;
import com.school.school_management.dto.LoginRequest;
import com.school.school_management.dto.RegisterRequest;
import com.school.school_management.jwt.JwtUtil;
import com.school.school_management.model.Role;
import com.school.school_management.model.User;
import com.school.school_management.repo.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

   private final UserRepository userRepository;
   private final PasswordEncoder passwordEncoder;
   private final JwtUtil jwtUtil;
   private AuthenticationManager authManager;
   private final CustomUserDetailsService userDetailsService;

   public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
      this.userRepository = userRepository;
      this.passwordEncoder = passwordEncoder;
      this.jwtUtil = jwtUtil;
      this.userDetailsService = userDetailsService;
      this.authManager = authenticationManager;
   }

   // REGISTER

   public AuthResponse register(RegisterRequest request) {

      //check username not token
      if (userRepository.existsByUsername(request.getUsername())) {
         throw new RuntimeException("Username already taken!");
      }

      //check email not taken
      if (userRepository.existsByEmail(request.getEmail())) {
         throw new RuntimeException("Email already registered");
      }

      //Create new user
      User user = new User(
              request.getUsername(),
              request.getEmail(),
              passwordEncoder.encode(request.getPassword()), //hash password before saving
              Role.USER
      );

      userRepository.save(user);

      //Generate jwt token for new user
      UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
      String token = jwtUtil.generateToken(userDetails);

      return new AuthResponse(
              token,
              user.getUsername(),
              user.getRole().name(),
              "Registration successful!"
      );
   }

   //LOGIN
   public AuthResponse login(LoginRequest request) {

      //Authenticate (throws if wrong credentials)
      authManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                      request.getUsername(),
                      request.getPassword()
              )
      );

      //Load user details
      UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

      //Generate jwt token
      String token = jwtUtil.generateToken(userDetails);

      //find user for role info
      User user = userRepository
              .findByUsername(request.getUsername())
              .orElseThrow();

      return new AuthResponse(
              token,
              user.getUsername(),
              user.getRole().name(),
              "Login successful!"
      );
   }
}
