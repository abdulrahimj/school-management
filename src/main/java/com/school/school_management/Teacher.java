package com.school.school_management;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teachers")
public class Teacher {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @NotBlank(message = "Name is required")
   @Column(nullable = false)
   private String name;

   @NotBlank(message = "Email is required")
   @Email(message = "Email must be valid")
   @Column(nullable = false, unique = true)
   private String email;

   @NotBlank(message = "Specification is required")
   @Column(nullable = false)
   private String specification;

   //Relationship: One teacher teaches Many courses
   @OneToMany(mappedBy = "teacher")
   private Set<Course> courses = new HashSet<>();
}
