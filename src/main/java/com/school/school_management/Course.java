package com.school.school_management;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @NotBlank(message = "Course name is required")
   @Column(nullable = false, unique = true)
   private String name;

   @Column
   private String description;

   //The relationship: Course has many students
   @ManyToMany(
           mappedBy = "courses",
           fetch = FetchType.LAZY
   )
   @JsonIgnore
   private Set<Student> students = new HashSet<>();

   public Course() {}

   public Course(Long id, String name, String description, Set<Student> students) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.students = students;
   }

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public Set<Student> getStudents() {
      return students;
   }

   public void setStudents(Set<Student> students) {
      this.students = students;
   }
}
