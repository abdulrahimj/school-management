package com.school.school_management.course;

import com.school.school_management.dto.PageResponse;
import com.school.school_management.student.Student;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

   private final CourseService courseService;

   public CourseController(CourseService courseService) {
      this.courseService = courseService;
   }

   //get all courses with pagination and sorting
   //GET /api/courses
   @GetMapping
   public PageResponse<Course> getAllCourses(
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "1") int size,
           @RequestParam(defaultValue = "id") String sortBy,
           @RequestParam(defaultValue = "asc") String sortDir
   ) {
      return courseService.getAllCourses(page, size, sortBy, sortDir);
   }

   //search courses with pagination
   //GET /api/courses/search?name=science&page=1&size=2
   @GetMapping("/search")
   public PageResponse<Course> searchCourses(
           @RequestParam String name,
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "5") int size,
           @RequestParam(defaultValue = "name") String sortBy,
           @RequestParam(defaultValue = "desc") String sortDir) {

      return courseService.searchCoursesByName(name, page, size, sortBy, sortDir);
   }

   //GEt /api/courses/1
   @GetMapping("/{id}")
   public Course getCourseById(@PathVariable Long id) {
      return courseService.getCourseById(id);
   }

   //POST /api/courses
   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Course createCourse(@Valid @RequestBody Course course) {
      return courseService.createCourse(course);
   }

   //POST /api/courses/1/enroll/2
   //Enroll student 2 in course 1
   @PostMapping("/{courseId}/enroll/{studentId}")
   public Student enrollStudent(
           @PathVariable Long courseId,
           @PathVariable Long studentId) {
      return courseService.enrollStudentInCourse(studentId, courseId);
   }

   //DELETE /api/courses/1/unenroll/2
   //Remove student 2 from course 1
   @DeleteMapping("/{courseId}/unenroll/{studentId}")
   public Student unenrollStudent(
           @PathVariable Long courseId,
           @PathVariable Long studentId) {
      return courseService.unenrollStudentFromCourse(studentId, courseId);
   }

   //GET /api/courses/1/students
   //Get all students in course 1
   @GetMapping("/{courseId}/students")
   public Set<Student> getStudentsInCourse(@PathVariable Long courseId) {
      return courseService.getStudentsInCourse(courseId);
   }

   //DELETE /api/courses/1
   @DeleteMapping("/{id}")
   @ResponseStatus(HttpStatus.NO_CONTENT)
   public void deleteCourse(@PathVariable Long id) {
      courseService.deleteCourse(id);
   }

   //POST /api/course/1/teacher/2
   //assign teacher 2 to course 1
   @PostMapping("/{courseId}/teacher/{teacherId}")
   public Course assignTeacher(
           @PathVariable Long courseId,
           @PathVariable Long teacherId) {

      return courseService.assignTeacherToCourse(courseId, teacherId);
   }
}
