package com.school.school_management;

import com.school.school_management.model.Student;
import com.school.school_management.repo.StudentRepository;
import com.school.school_management.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {

   @Mock
   private StudentRepository studentRepository;
   @InjectMocks
   private StudentService studentService;

   //Test data
   private Student alice;
   private Student bob;

   @BeforeEach
   void setUp() {
      alice = new Student("Alice", "alice@gmail.com", 20);
      alice.setId(1L);
      bob = new Student("Bob", "bob@gmail.com", 22);
      bob.setId(2L);
   }

   //TEST: getAllStudents
   @Test
   @DisplayName("Should return all students")
   void shouldReturnAllStudents() {

      // ARRANGE: Set up the mock behavior
      Pageable pageable = PageRequest.of(0, 10, Sort.by("name").ascending());
      Page<Student> studentPage = new PageImpl<>(List.of(alice, bob));

      when(studentRepository.findAll(any(Pageable.class)))
              .thenReturn(studentPage);

      // ACT: Call the method we're testing
      PageResponse<Student> result = studentService.getAllStudents(
              0,
              10,
              "name",
              "asc"
      );

      // ASSERT: Check the result is correct
      assertThat(result.getContent()).hasSize(2);
      assertThat(result.getContent().get(0).getName()).isEqualTo("Alice");
      assertThat(result.getContent().get(1).getName()).isEqualTo("Bob");

      // VERIFY: Was repository actually called?
      verify(studentRepository, times(1)).findAll(any(Pageable.class));
   }

   //TEST: getStudentById - SUCCESS
   @Test
   @DisplayName("Should return student when ID exists")
   void shouldReturnStudentWhenIdExists() {

      //ARRANGE
      when(studentRepository.findById(1L))
              .thenReturn(Optional.of(alice));

      //ACT
      Student result = studentService.getStudentById(1L);

      //ASSERT
      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(1L);
      assertThat(result.getName()).isEqualTo("Alice");
      assertThat(result.getEmail()).isEqualTo("alice@gmail.com");
   }

   //TEST: getStudentById -  NOT FOUND
   @Test
   @DisplayName("Should throw exception when student not found")
   void shouldThrowExceptionWhenStudentNotFound() {

      //ARRANGE
      when(studentRepository.findById(999L))
              .thenReturn(Optional.empty());

      //ACT & ASSERT
      assertThatThrownBy(() ->
              studentService.getStudentById(999L))
              .isInstanceOf(RuntimeException.class)
              .hasMessageContaining("999");
   }

   //TEST: createStudent - SUCCESS
   @Test
   @DisplayName("Should create student successfully")
   void shouldCreateStudentSuccessfully() {

      //ARRANGE
      Student newStudent = new Student("Charlie", "charlie@gmail.com", 21);

      //Email does not exist yet
      when(studentRepository.findByEmail("charlie@gmail.com"))
              .thenReturn(Optional.empty());

      //When save() is called, return the student with id
      when(studentRepository.save(newStudent))
              .thenReturn(newStudent);

      //ACT
      Student result = studentService.createStudent(newStudent);

      //ASSERT
      assertThat(result).isNotNull();
      assertThat(result.getName()).isEqualTo("Charlie");

      //VERIFY
      verify(studentRepository, times(1)).save(newStudent);
   }

   //TEST: createStudent - Duplicate Email
   @Test
   @DisplayName("Should throw exception when email already exists")
   void shouldThrowExceptionWhenEmailAlreadyExists() {

      //ARRANGE
      Student duplicate = new Student("Alice Copy", "alice@gmail.com", 25);

      //Email already exists
      when(studentRepository.findByEmail("alice@gmail.com"))
              .thenReturn(Optional.of(alice));

      //ACT & ASSERT
      assertThatThrownBy(() -> studentService.createStudent(duplicate))
              .isInstanceOf(RuntimeException.class)
              .hasMessageContaining("alice@gmail.com");

      //VERIFY
      verify(studentRepository, never()).save(any());
   }

   //TEST: deleteStudent - SUCCESS
   @Test
   @DisplayName("Should delete student when ID exists")
   void shouldDeleteStudentWhenExists() {

      //ARRANGE
      when(studentRepository.findById(1L))
              .thenReturn(Optional.of(alice));

      //doNothing = deleteById does not return anything
      doNothing().when(studentRepository).deleteById(1L);

      //ACT
      studentService.deleteStudent(1L);

      //VERIFY
      verify(studentRepository, times(1)).deleteById(1L);
   }

   //TEST: updateStudent
   @Test
   @DisplayName("Should update student successfully")
   void shouldUpdateStudentSuccessfully() {

      //ARRANGE
      Student updatedData = new Student(
              "Alice Updated",
              "alice.new@gmail.com",
              21
      );

      when(studentRepository.findById(1L))
              .thenReturn(Optional.of(alice));

      when(studentRepository.save(any(Student.class)))
              .thenAnswer(invocation ->
                      invocation.getArgument(0)
              );
      //When save() is called with any student, return that same student

      //ACT
      Student result = studentService.updateStudent(1L, updatedData);

      //ASSERT
      assertThat(result.getName()).isEqualTo("Alice Updated");
      assertThat(result.getEmail()).isEqualTo("alice.new@gmail.com");
      assertThat(result.getAge()).isEqualTo(21);
   }
}
