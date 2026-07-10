package com.school.school_management.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

   //Reads jwt.secret from application.properties
   @Value("${jwt.secret}")
   private String secret;

   //Read jwt.expiration from application.properties
   @Value("${jwt.expiration}")
   private Long expiration;

   //Create the secret key for signing
   private SecretKey getSigningKey() {
      return Keys.hmacShaKeyFor(secret.getBytes());
   }

   //==========================
   // GENERATE TOKEN
   //==========================
   public String generateToken(UserDetails userDetails) {

      Map<String, Object> claims = new HashMap<>();
      //Add role to token payload
      claims.put("role", userDetails.getAuthorities()
              .iterator().next().getAuthority());

      return Jwts.builder()
              .claims(claims)  //add extra data (role)
              .subject(userDetails.getUsername())  //who this token is for (username)
              .issuedAt(new Date())  //when was this token created
              .expiration(new Date(System.currentTimeMillis() + expiration))  //when does this token expire (current time + 24 hours)
              .signWith(getSigningKey())  //sign with secret key (proves the token is genuine)
              .compact();  //build and return the token string
   }

   //=============================
   //EXTRACT USERNAME FROM TOKEN
   //=============================
   public String extractUsername(String token) {
      return extractClaims(token).getSubject();
   }

   //============================
   // CHECK IF TOKEN IS VALID
   //============================
   public boolean isTokenValid(String token, UserDetails userDetails) {
      String username = extractUsername(token);
      return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
      //Token is valid if: username in token matches the user, token has not expired
   }

   //================================
   // CHECK IF TOKEN IS EXPIRED
   //================================
   private boolean isTokenExpired(String token) {
      return extractClaims(token)
              .getExpiration()
              .before(new Date());

      //Is the expiration date before now? Yes = expired
   }

   //==============================
   //EXTRACT ALL CLAIMS FROM TOKEN
   //==============================
   private Claims extractClaims(String token) {
      return Jwts.parser()
              .verifyWith(getSigningKey())  //verify using our secret key
              .build()
              .parseSignedClaims(token)
              .getPayload();
   }
}
