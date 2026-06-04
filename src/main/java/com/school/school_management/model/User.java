package com.school.school_management.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @NotBlank(message = "Username is required")
   @Column(nullable = false, unique = true)
   private String username;

   @NotBlank(message = "Email is required")
   @Email(message = "Email must be valid")
   @Column(nullable = false, unique = true)
   private String email;

   @NotBlank(message = "Password is required")
   @Size(min = 6, message = "Password must be at least 6 characters")
   @Column(nullable = false)
   private String password;
   //Never store plain txt passwords
   //Store hashed (encrypted) passwords

   @Enumerated(EnumType.STRING)
   @Column(nullable = false)
   private Role role;

   public User() {}

   public User(String username, String email, String password, Role role) {
      this.username = username;
      this.email = email;
      this.password = password;
      this.role = role;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public Role getRole() {
      return role;
   }

   public void setRole(Role role) {
      this.role = role;
   }
}
