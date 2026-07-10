package com.school.school_management.securityConfig;

import com.school.school_management.jwt.JwtAuthenticationFilter;
import com.school.school_management.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

   private final CustomUserDetailsService userDetailsService;
   private final JwtAuthenticationFilter jwtAuthFilter;

   public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthFilter) {
      this.userDetailsService = userDetailsService;
      this.jwtAuthFilter = jwtAuthFilter;
   }

   //SECURITY RULES
   @Bean
   public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {

      http
              .csrf(csrf -> csrf.disable())
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/api/auth/**").permitAll()
                      .requestMatchers(HttpMethod.POST,
                              "/api/students/**",
                              "/api/courses/**",
                              "/api/teachers/**"
                      ).hasRole("ADMIN")

                      .requestMatchers(HttpMethod.PUT,
                              "/api/students/**",
                              "/api/courses/**",
                              "/api/teachers/**"
                      ).hasRole("ADMIN")

                      .requestMatchers(HttpMethod.DELETE,
                              "/api/students/**",
                              "/api/courses/**",
                              "/api/teachers/**"
                      ).hasRole("ADMIN")

                      .requestMatchers(
                              "/swagger-ui/**",
                              "/swagger-ui.html",
                              "/v3/api-docs/**",
                              "/v3/api-docs",
                              "/swagger-resources/**",
                              "/webjars/**"
                      ).permitAll()

                      .anyRequest().authenticated()
              )

              //use stateless sessions (jwt, not cookies)
              //spring boot wont store sessions
              //every request must have jwt token
              .sessionManagement(session -> session
                      .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              )

              //user authentication provider
              .authenticationProvider(authenticationProvider())

              //add jwt filter before spring's default filter
              .addFilterBefore(
                      jwtAuthFilter,
                      UsernamePasswordAuthenticationFilter.class
              );

      return http.build();
   }

   //PASSWORD ENCODER (BCrypt)
   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }

   //AUTHENTICATION PROVIDER
   @Bean
   public AuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
      provider.setPasswordEncoder(passwordEncoder());
      return provider;
   }

   //AUTHENTICATION MANAGER
   @Bean
   public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
      return config.getAuthenticationManager();
   }
}
