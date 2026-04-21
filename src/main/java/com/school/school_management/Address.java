package com.school.school_management;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "addresses")
public class Address {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @NotBlank(message = "Street is required")
   @Column(nullable = false)
   private String street;

   @NotBlank(message = "City is required")
   @Column(nullable = false)
   private String city;

   @NotBlank(message = "Country is required")
   @Column(nullable = false)
   private String country;

   //THE RELATIONSHIP: Address belongs to ONE student
   @OneToOne
   @JoinColumn(name = "student_id")
   //Creates "student_id" column in addresses table
   //This is the foreign key linking to student table
   @JsonBackReference
   //When converting to JSON, skip this field
   //Prevent infinite loop
   private Student student;

   //No arg constructor (for JPA)
   public Address() {}

   public Address(String street, String city, String country) {
      this.street = street;
      this.city = city;
      this.country = country;
   }

   //Getters and Setters
   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getStreet() {
      return street;
   }

   public void setStreet(String street) {
      this.street = street;
   }

   public String getCity() {
      return city;
   }

   public void setCity(String city) {
      this.city = city;
   }

   public String getCountry() {
      return country;
   }

   public void setCountry(String country) {
      this.country = country;
   }

   public Student getStudent() {
      return student;
   }

   public void setStudent(Student student) {
      this.student = student;
   }
}
