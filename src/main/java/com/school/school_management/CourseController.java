package com.school.school_management;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/courses")
public class CourseController {

   private final CourseService courseService;

   public CourseController(CourseService courseService) {
      this.courseService = courseService;
   }

   //GET /api/courses
   @GetMapping
   public List<Course> getAllCourses() {
      return courseService.getAllCourses();
   }

   //GEt /api/courses/1
   @GetMapping("/{id}")
   public Course getCourseById(@PathVariable Long Id) {
      return courseService.getCourseById(Id);
   }

   //POST /api/courses
   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Course createCourses(@Valid @RequestBody Course course) {
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
}
