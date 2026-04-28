package com.school.school_management;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students")
public class Student {

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

   @Min(value = 1, message = "Age must be at least 1")
   @Column(nullable = false)
   private int age;

   //THE RELATIONSHIP: Student has one address
   @OneToOne(
           mappedBy = "student",
           cascade = CascadeType.ALL,
           fetch = FetchType.LAZY,
           optional = true
   )
   @JsonManagedReference
   private Address address;

   //Student has many courses
   @ManyToMany(
           fetch = FetchType.LAZY,
           cascade = {CascadeType.PERSIST, CascadeType.MERGE}
           //Only cascade SAVE and UPDATE, not delete
           //Deleting student does not delete courses
   )
   @JoinTable(
           name = "student_courses",
           //Name of the JOIN TABLE in database

           joinColumns = @JoinColumn(name = "student_id"),
           //column in join table for student ID

           inverseJoinColumns = @JoinColumn(name = "course_id")
           //column in join table for course ID
   )
   private Set<Course> courses = new HashSet<>();

   public Student() {}

   public Student(String name, String email, int age) {
      this.name = name;
      this.email = email;
      this.age = age;
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

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public int getAge() {
      return age;
   }

   public void setAge(int age) {
      this.age = age;
   }

   public Address getAddress() {
      return address;
   }

   public void setAddress(Address address) {
      this.address = address;
   }

   public Set<Course> getCourse() {
      return courses;
   }

   public void setCourse(Set<Course> course) {
      this.courses = course;
   }
}
