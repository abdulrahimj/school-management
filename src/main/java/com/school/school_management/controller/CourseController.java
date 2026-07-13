package com.school.school_management.controller;

import com.school.school_management.dto.request.CourseRequest;
import com.school.school_management.dto.response.CourseResponse;
import com.school.school_management.dto.response.PageResponse;
import com.school.school_management.dto.response.StudentResponse;
import com.school.school_management.service.CourseService;
import com.school.school_management.model.Student;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/courses")
@Tag(
        name = "Courses",
        description = "Endpoints for managing courses"
)
@SecurityRequirement(name = "bearerAuth")  //all endpoints in this controller need jwt token. shows lock icon in swagger UI
public class CourseController {

   private final CourseService courseService;

   public CourseController(CourseService courseService) {
      this.courseService = courseService;
   }

   //get all courses with pagination and sorting
   //GET /api/courses
   @GetMapping
   @Operation(
           summary = "Get all courses",
           description = "Returns paginated list of all courses. " +
                           "Requires authentication."
   )
   @ApiResponses({
           @ApiResponse(
                   responseCode = "200",
                   description = "Courses retrieved successfully"
           ),
           @ApiResponse(
                   responseCode = "401",
                   description = "Unauthorized - JWT token required", content = @Content(schema = @Schema(hidden = true))
           )
   })
   public PageResponse<CourseResponse> getAllCourses(

           @Parameter(description = "Page number start at 0")
           @RequestParam(defaultValue = "0") int page,

           @Parameter(description = "Number of courses per page")
           @RequestParam(defaultValue = "1") int size,

           @Parameter(description = "Field to sort by")
           @RequestParam(defaultValue = "id") String sortBy,

           @Parameter(description = "Sort direction: asc or desc")
           @RequestParam(defaultValue = "asc") String sortDir
   ) {
      return courseService.getAllCourses(page, size, sortBy, sortDir);
   }

   //search courses with pagination
   //GET /api/courses/search?name=science&page=1&size=2
   @GetMapping("/search")
   @Operation(
           summary = "Search courses by name",
           description = "Returns paginated list of courses whose course name contains the search term"
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Search term successfully retrieved."),
           @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
   })
   public PageResponse<CourseResponse> searchCourses(
           @Parameter(description = "Course name to search for")
           @RequestParam String name,

           @Parameter(description = "Page number start at 0")
           @RequestParam(defaultValue = "0") int page,

           @Parameter(description = "Number of courses per page")
           @RequestParam(defaultValue = "5") int size,

           @Parameter(description = "Field to sort by")
           @RequestParam(defaultValue = "name") String sortBy,

           @Parameter(description = "Sort direction: asc or desc")
           @RequestParam(defaultValue = "desc") String sortDir) {

      return courseService.searchCoursesByName(name, page, size, sortBy, sortDir);
   }

   //GEt /api/courses/1
   @GetMapping("/{id}")
   @Operation(
           summary = "Search courses by ID",
           description = "Return a single course by their ID"
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Course found"),
           @ApiResponse(responseCode = "404", description = "Course not found", content = @Content(schema = @Schema(hidden = true)))
   })
   public CourseResponse getCourseById(
           @Parameter(description = "Course ID", required = true, example = "1")
           @PathVariable Long id) {
      return courseService.getCourseById(id);
   }

   //POST /api/courses
   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   @Operation(
           summary = "Create a new course",
           description = "Only ADMIN can create a new course."
   )
   @ApiResponses({
           @ApiResponse(
                   responseCode = "200",
                   description = "Course created successfully"
           ),
           @ApiResponse(
                   responseCode = "400",
                   description = "Validation failed"
           ),
           @ApiResponse(
                   responseCode = "403",
                   description = "Forbidden - ADMIN role required."
           )
   })
   public CourseResponse createCourse(@Valid @RequestBody CourseRequest request) {
      return courseService.createCourse(request);
   }

   //POST /api/courses/1/enroll/2
   //Enroll student 2 in course 1
   @PostMapping("/{courseId}/enroll/{studentId}")
   @Operation(
           summary = "Enroll student to course",
           description = "Add or update the student for a course. ADMIN only"
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Student added successfully"),
           @ApiResponse(responseCode = "404", description = "Course not found"),
           @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
   })
   public Student enrollStudent(
           @Parameter(description = "Course ID")
           @PathVariable Long courseId,
           @Parameter(description = "Student ID")
           @PathVariable Long studentId) {
      return courseService.enrollStudentInCourse(studentId, courseId);
   }

   //DELETE /api/courses/1/unenroll/2
   //Remove student 2 from course 1
   @DeleteMapping("/{courseId}/unenroll/{studentId}")
   @Operation(
           summary = "Remove student from a course",
           description = "Only ADMIN can remove student from a course."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Student removed successful"),
           @ApiResponse(responseCode = "404", description = "Course not found"),
           @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN required")
   })
   public Student unenrollStudent(
           @Parameter(description = "Course ID")
           @PathVariable Long courseId,
           @Parameter(description = "Student ID")
           @PathVariable Long studentId) {
      return courseService.unenrollStudentFromCourse(studentId, courseId);
   }

   //GET /api/courses/1/students
   //Get all students in course 1
   @GetMapping("/{courseId}/students")
   @Operation(
           summary = "Get all students in a course",
           description = "Returns the set of students enrolled in the given course. Requires authentication."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
           @ApiResponse(responseCode = "404", description = "Course not found", content = @Content(schema = @Schema(hidden = true))),
           @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required", content = @Content(schema = @Schema(hidden = true)))
   })
   public Set<StudentResponse> getStudentsInCourse(
           @Parameter(description = "Course ID", required = true, example = "1")
           @PathVariable Long courseId) {
      return courseService.getStudentsInCourse(courseId);
   }

   //DELETE /api/courses/1
   @DeleteMapping("/{id}")
   @ResponseStatus(HttpStatus.NO_CONTENT)
   @Operation(
           summary = "Delete a course",
           description = "Only ADMIN can delete a course."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
           @ApiResponse(responseCode = "404", description = "Course not found", content = @Content(schema = @Schema(hidden = true))),
           @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
   })
   public void deleteCourse(
           @Parameter(description = "Course ID", required = true, example = "1")
           @PathVariable Long id) {
      courseService.deleteCourse(id);
   }

   //POST /api/course/1/teacher/2
   //assign teacher 2 to course 1
   @PostMapping("/{courseId}/teacher/{teacherId}")
   @Operation(
           summary = "Assign a teacher to a course",
           description = "Assigns the given teacher to the given course. ADMIN only."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Teacher assigned successfully"),
           @ApiResponse(responseCode = "404", description = "Course or teacher not found", content = @Content(schema = @Schema(hidden = true))),
           @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
   })
   public CourseResponse assignTeacher(
           @Parameter(description = "Course ID", required = true, example = "1")
           @PathVariable Long courseId,
           @Parameter(description = "Teacher ID", required = true, example = "2")
           @PathVariable Long teacherId) {

      return courseService.assignTeacherToCourse(courseId, teacherId);
   }
}
