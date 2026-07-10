package com.school.school_management;

import com.school.school_management.dto.PageResponse;
import com.school.school_management.model.Course;
import com.school.school_management.model.Teacher;
import com.school.school_management.repo.TeacherRepository;
import com.school.school_management.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

   @Mock
   private TeacherRepository teacherRepository;

   @InjectMocks
   private TeacherService teacherService;

   //Test data
   private Teacher drSmith;
   private Teacher profJohnson;

   @BeforeEach
   void setUp() {
      drSmith = new Teacher("Dr. Smith", "smith@school.com", "Mathematics");
      drSmith.setId(1L);

      profJohnson = new Teacher("Prof. Johnson", "johnson@school.com", "Science");
      profJohnson.setId(2L);

      //add courses to drSmith for testing
     Course math = new Course("Mathematics", "Advanced Math");
     math.setId(1L);

     Course algebra = new Course("Algebra", "Basic Algebra");
     algebra.setId(2L);

     drSmith.setCourses(Set.of(math, algebra));
     //drSmith teaches math and algebra
   }

   @Test
   @DisplayName("Should return all teachers")
   void shouldReturnAllTeachers() {

      //arrange
      Page<Teacher> page = new PageImpl<>(List.of(drSmith, profJohnson));
      when(teacherRepository.findAll(any(Pageable.class)))
              .thenReturn(page);

      //act
      PageResponse<Teacher> result = teacherService.getAllTeachers(0, 10, "name", "asc");

      //assert
      assertThat(result).isNotNull();
      assertThat(result.getContent()).hasSize(2);
      assertThat(result.getContent().get(0).getName())
              .isEqualTo("Dr. Smith");
      assertThat(result.getContent().get(1).getName())
              .isEqualTo("Prof. Johnson");

      //verify
      verify(teacherRepository, times(1)).findAll(any(Pageable.class));
   }

   @Test
   @DisplayName("Should return empty list when no teachers exist")
   void shouldReturnEmptyListWhenNoTeachersExist() {

      // ARRANGE
      Page<Teacher> page = new PageImpl<>(List.of());
      when(teacherRepository.findAll(any(Pageable.class)))
              .thenReturn(page);

      // ACT
      PageResponse<Teacher> result = teacherService.getAllTeachers(0, 10, "name", "asc");

      // ASSERT
      assertThat(result).isNotNull();
      assertThat(result.getContent()).isEmpty();

      verify(teacherRepository, times(1)).findAll(any(Pageable.class));
   }

   @Test
   @DisplayName("Should return teacher when ID exists")
   void shouldReturnTeacherWhenIdExists() {

      // ARRANGE
      when(teacherRepository.findById(1L))
              .thenReturn(Optional.of(drSmith));

      // ACT
      Teacher result = teacherService.getTeacherById(1L);

      // ASSERT
      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(1L);
      assertThat(result.getName()).isEqualTo("Dr. Smith");
      assertThat(result.getEmail())
              .isEqualTo("smith@school.com");
      assertThat(result.getSpecialization())
              .isEqualTo("Mathematics");
   }

   @Test
   @DisplayName("Should throw exception when teacher ID not found")
   void shouldThrowExceptionWhenTeacherIdNotFound() {

      // ARRANGE
      when(teacherRepository.findById(999L))
              .thenReturn(Optional.empty());

      // ACT & ASSERT
      assertThatThrownBy(() ->
              teacherService.getTeacherById(999L)
      )
              .isInstanceOf(RuntimeException.class)
              .hasMessageContaining("999");
      // ↑ Error message MUST contain the ID
      //   so developers know WHICH ID was not found!
   }

   @Test
   @DisplayName("Should create teacher successfully")
   void shouldCreateTeacherSuccessfully() {

      //arrange
      Teacher newTeacher = new Teacher("Dr. Brown", "brown@school.com", "History");

      //email does not exist yet
      when(teacherRepository.findByEmail("brown@school.com"))
              .thenReturn(Optional.empty());

      //when save() is called, return the teacher
      when(teacherRepository.save(newTeacher))
              .thenReturn(newTeacher);

      //act
      Teacher result = teacherService.createTeacher(newTeacher);

      //assert
      assertThat(result).isNotNull();
      assertThat(result.getName()).isEqualTo("Dr. Brown");
      assertThat(result.getEmail()).isEqualTo("brown@school.com");
      assertThat(result.getSpecialization()).isEqualTo("History");

      //verify
      verify(teacherRepository, times(1)).save(newTeacher);
   }

   @Test
   @DisplayName("Should throw exception when email already exists")
   void shouldThrowExceptionWhenEmailAlreadyExists() {

      // ARRANGE
      Teacher duplicateEmail = new Teacher(
              "Another Smith",
              "smith@school.com", // ← Same email as drSmith!
              "Physics"
      );

      // Email ALREADY exists!
      when(teacherRepository.findByEmail("smith@school.com"))
              .thenReturn(Optional.of(drSmith));

      // ACT & ASSERT
      assertThatThrownBy(() ->
              teacherService.createTeacher(duplicateEmail)
      )
              .isInstanceOf(RuntimeException.class)
              .hasMessageContaining("smith@school.com");

      // VERIFY save was NEVER called!
      // (We stopped before saving duplicate!)
      verify(teacherRepository, never()).save(any());
   }

   @Test
   @DisplayName("Should return courses for teacher")
   void shouldReturnCoursesForTeacher() {

      // ARRANGE
      when(teacherRepository.findById(1L))
              .thenReturn(Optional.of(drSmith));

      // ACT
      Set<Course> result =
              teacherService.getCoursesByTeacher(1L);

      // ASSERT
      assertThat(result).isNotNull();
      assertThat(result).hasSize(2);
      assertThat(result)
              .extracting(Course::getName)
              // ↑ Extract just the names from each course
              .containsExactlyInAnyOrder(
                      "Mathematics",
                      "Algebra"
              );
      // ↑ Both courses are there (order doesn't matter)
   }

   @Test
   @DisplayName("Should return empty list when teacher has no courses")
   void shouldReturnEmptyListWhenTeacherHasNoCourses() {

      // ARRANGE
      // profJohnson has no courses assigned
      profJohnson.setCourses(Set.of());

      when(teacherRepository.findById(2L))
              .thenReturn(Optional.of(profJohnson));

      // ACT
      Set<Course> result =
              teacherService.getCoursesByTeacher(2L);

      // ASSERT
      assertThat(result).isNotNull();
      assertThat(result).isEmpty();
   }

   @Test
   @DisplayName("Should throw exception when teacher not found for courses")
   void shouldThrowExceptionWhenTeacherNotFoundForCourses() {

      // ARRANGE
      when(teacherRepository.findById(999L))
              .thenReturn(Optional.empty());

      // ACT & ASSERT
      assertThatThrownBy(() ->
              teacherService.getCoursesByTeacher(999L)
      )
              .isInstanceOf(RuntimeException.class)
              .hasMessageContaining("999");
   }

   @Test
   @DisplayName("Should delete teacher when exists")
   void shouldDeleteTeacherWhenExists() {

      // ARRANGE
      when(teacherRepository.findById(1L))
              .thenReturn(Optional.of(drSmith));

      doNothing().when(teacherRepository).deleteById(1L);
      // ↑ deleteById returns void
      // ↑ "When called, do nothing" (it's a fake!)

      // ACT
      teacherService.deleteTeacher(1L);

      // ASSERT & VERIFY
      // No return value, so we VERIFY the calls!
      verify(teacherRepository, times(1)).findById(1L);
      // ↑ First it checked if teacher exists

      verify(teacherRepository, times(1)).deleteById(1L);
      // ↑ Then it deleted the teacher
   }

   @Test
   @DisplayName("Should throw exception when deleting non-existing teacher")
   void shouldThrowExceptionWhenDeletingNonExistingTeacher() {

      // ARRANGE
      when(teacherRepository.findById(999L))
              .thenReturn(Optional.empty());

      // ACT & ASSERT
      assertThatThrownBy(() ->
              teacherService.deleteTeacher(999L)
      )
              .isInstanceOf(RuntimeException.class)
              .hasMessageContaining("999");

      // VERIFY deleteById was NEVER called!
      verify(teacherRepository, never()).deleteById(any());
      // ↑ If teacher not found, we should NEVER delete!
      // ↑ This proves your code stops early on error!
   }
}
