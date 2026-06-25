package com.school.school_management;

import com.school.school_management.model.Student;
import com.school.school_management.repo.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class StudentRepoTest {

   @Autowired
   private StudentRepository studentRepository;

   @BeforeEach
   void setUp() {
      //clear DB before each test
      studentRepository.deleteAll();

      //add test data
      studentRepository.save(
              new Student("Alice Johnson", "alice@gmail.com", 20)
      );
      studentRepository.save(
              new Student("Alice Smith", "alice.smith@gmail.com", 22)
      );
      studentRepository.save(
              new Student("Bob Brown", "bob@gmail.com", 19)
      );
   }

   //TEST: findByEmail
   @Test
   @DisplayName("Should find student by email")
   void shouldFindStudentByEmail() {

      Optional<Student> result = studentRepository.findByEmail("alice@gmail.com");

      assertThat(result).isPresent();
      assertThat(result.get().getName()).isEqualTo("Alice Johnson");
   }

   @Test
   @DisplayName("Should return empty when email not found")
   void shouldReturnEmptyWhenEmailNotFound() {

      Optional<Student> result = studentRepository.findByEmail("notexist@gmail.com");

      assertThat(result).isEmpty();
   }

   //TEST: findByNameContaining
   @Test
   @DisplayName("Should find students by name containing")
   void shouldFindStudentsByNameContaining() {

      List<Student> results = studentRepository
              .findByNameContaining("Alice", Pageable.unpaged())
              .getContent();
      
      assertThat(results).hasSize(2);
      assertThat(results)
              .extracting(Student::getName)
              .containsExactlyInAnyOrder("Alice Johnson", "Alice Smith");
   }

   //TEST: save and retrieve
   @Test
   @DisplayName("Should save and retrieve student correctly")
   void shouldSaveAndRetrieveStudent() {

      Student newStudent = new Student("Charlie", "charlie@gmail.com", 25);

      Student saved = studentRepository.save(newStudent);

      assertThat(saved.getId()).isNotNull();
      //ID should be auto-generated (not null)

      Optional<Student> found = studentRepository.findById(saved.getId());

      assertThat(found).isPresent();
      assertThat(found.get().getName()).isEqualTo("Charlie");
   }

   //TEST: Delete
   @Test
   @DisplayName("Should delete student by ID")
   void shouldDeleteStudentById() {

      Student student = studentRepository.findByEmail("bob@gmail.com").get();

      studentRepository.deleteById(student.getId());

      Optional<Student> found = studentRepository.findById(student.getId());

      assertThat(found).isEmpty();
      //should be gone after deletion
   }

   //TEST: Count
   @Test
   @DisplayName("Should return correct count")
   void shouldReturnCorrectCount() {

      long count = studentRepository.count();
      assertThat(count).isEqualTo(3);
      //we added 3 students in setup()
   }
}
