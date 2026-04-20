package com.school.school_management;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

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
}
