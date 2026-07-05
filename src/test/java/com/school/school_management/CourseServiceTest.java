package com.school.school_management;

import com.school.school_management.dto.PageResponse;
import com.school.school_management.model.Course;
import com.school.school_management.model.Student;
import com.school.school_management.model.Teacher;
import com.school.school_management.repo.CourseRepository;
import com.school.school_management.repo.StudentRepository;
import com.school.school_management.repo.TeacherRepository;
import com.school.school_management.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

   @Mock
   private CourseRepository courseRepository;

   @Mock
   private StudentRepository studentRepository;

   @Mock
   private TeacherRepository teacherRepository;

   @InjectMocks
   private CourseService courseService;

   //Test data shared across all tests
   private Course math;
   private Course science;
   private Teacher drSmith;
   private Student alice;

   @BeforeEach
   void setUp() {
      //create test courses
      math = new Course("Mathematics", "Advanced Math");
      math.setId(1L);

      science = new Course("Science", "Physics & Chemistry");
      science.setId(2L);

      //create test teacher
      drSmith = new Teacher("Dr. Smith", "smith@school.com", "Mathematics");
      drSmith.setId(1L);

      //create test student
      alice = new Student("Alice", "alice@email.com", 20);
      alice.setId(1L);
   }

   @Test
   @DisplayName("Should return all courses")
   void shouldReturnAllCourses() {

      //arrange - tell the fake repo what to return
      Page<Course> page = new PageImpl<>(List.of(math, science));
      when(courseRepository.findAll(any(Pageable.class)))
              .thenReturn(page);

      //act - call the real method
      PageResponse<Course> response = courseService.getAllCourses(0, 10, "id", "asc");
      List<Course> result = response.getContent();

      //assert - check the results
      assertThat(result).isNotNull();
      assertThat(result).hasSize(2);
      assertThat(result.get(0).getName())
              .isEqualTo("Mathematics");
      assertThat(result.get(1).getName())
              .isEqualTo("Science");

      //verify repo was called exactly once
      verify(courseRepository, times(1)).findAll(any(Pageable.class));
   }

   @Test
   @DisplayName("Should return empty list when no courses exits")
   void shouldReturnEmptyListWhenNoCoursesExists() {

      //arrange
      Page<Course> page = new PageImpl<>(List.of());
      when(courseRepository.findAll(any(Pageable.class)))
              .thenReturn(page);

      //act
      PageResponse<Course> response = courseService.getAllCourses(0, 10, "id", "asc");
      List<Course> result = response.getContent();

      //assert
      assertThat(result).isNotNull();
      assertThat(result).isEmpty();

      verify(courseRepository, times(1)).findAll(any(Pageable.class));
   }

   @Test
   @DisplayName("Should return course when ID exists")
   void shouldReturnCourseWhenIdExists() {

      // ARRANGE
      when(courseRepository.findById(1L))
              .thenReturn(Optional.of(math));
      // ↑ "When someone asks for ID 1, give them math"

      // ACT
      Course result = courseService.getCourseById(1L);

      // ASSERT
      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(1L);
      assertThat(result.getName()).isEqualTo("Mathematics");
      assertThat(result.getDescription())
              .isEqualTo("Advanced Math");
   }

   @Test
   @DisplayName("Should throw exception when course ID not found")
   void shouldThrowExceptionWhenCourseIdNotFound() {

      // ARRANGE
      when(courseRepository.findById(999L))
              .thenReturn(Optional.empty());
      // ↑ "When someone asks for ID 999, return nothing"

      // ACT & ASSERT together
      assertThatThrownBy(() ->
              courseService.getCourseById(999L)
      )
              .isInstanceOf(RuntimeException.class)
              .hasMessageContaining("999");
      // ↑ "Calling getCourseById(999) MUST throw
      //    RuntimeException with '999' in the message"
   }
}
