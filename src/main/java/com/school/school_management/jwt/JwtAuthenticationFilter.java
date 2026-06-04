package com.school.school_management.jwt;

import com.school.school_management.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
   //OncePerRequestFilter = runs ONCE per request

   private final JwtUtil jwtUtil;
   private final CustomUserDetailsService userDetailsService;

   public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
      this.jwtUtil = jwtUtil;
      this.userDetailsService = userDetailsService;
   }

   @Override
   protected void doFilterInternal(
           HttpServletRequest request,
           HttpServletResponse response,
           FilterChain filterChain)
           throws ServletException, IOException {

      //Step 1: get the authorization header
      String authHeader = request.getHeader("Authorization");

      //step 2: check if header exists and starts with "Bearer"
      if (authHeader == null || !authHeader.startsWith("Bearer")) {

         //no token found -> continue without authentication
         filterChain.doFilter(request, response);
         return;
      }

      //step 3: extract token (remove "Bearer" prefix)
      String token = authHeader.substring(7);

      //step 4: extract username from token
      String username = jwtUtil.extractUsername(token);

      //step 5: if username found and not yet authenticated
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

         //load user from database
         UserDetails userDetails = userDetailsService.loadUserByUsername(username);

         //validate token
         if (jwtUtil.isTokenValid(token, userDetails)) {

            //create authentication object
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            //tell spring security: this user is authenticated
            SecurityContextHolder.getContext().setAuthentication(authToken);
         }
      }

      //continue to next filter/controller
      filterChain.doFilter(request, response);
   }
}
