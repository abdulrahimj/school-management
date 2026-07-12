package com.school.school_management.controller;

import com.school.school_management.dto.request.StudentRequest;
import com.school.school_management.dto.response.PageResponse;
import com.school.school_management.dto.response.StudentResponse;
import com.school.school_management.model.Address;
import com.school.school_management.model.Student;
import com.school.school_management.service.StudentService;
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

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Endpoints for managing students")
@SecurityRequirement(name = "bearerAuth") //all endpoints in this controller need jwt token. shows lock icon in swagger UI
public class StudentController {

   private final StudentService studentService;

   public StudentController(StudentService studentService) {
      this.studentService = studentService;
   }


   //----------------------------------------------
   //Get all students (with pagination + sorting)
   //---------------------------------------------
   //GET /api/students
   //GET /api/students?page=0&size=10
   //GET /api/students?page=1&size=5sortBy=name&sortDir=asc
   @GetMapping
   @Operation(
           summary = "Get all students",
           description = "Returns paginated list of all students. " +
                         "Requires authentication."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
           @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required", content = @Content(schema = @Schema(hidden = true)))
   })
   public PageResponse<StudentResponse> getAllStudents(
           @Parameter(description = "Page number (starts at 0)")
           @RequestParam(defaultValue = "0") int page,

           @Parameter(description = "Number of students per page")
           @RequestParam(defaultValue = "10") int size,

           @Parameter(description = "Field to sort by")
           @RequestParam(defaultValue = "id") String sortBy,

           @Parameter(description = "Sort direction: asc or desc")
           @RequestParam(defaultValue = "asc") String sortDir
   ) {
      return studentService.getAllStudents(page, size, sortBy, sortDir);
   }

   //-----------------------------------------------
   // Search by name (with pagination)
   //----------------------------------------------
   //GET /api/students/search?name=ali&page=0&size=5
   @GetMapping("/search")
   @Operation(summary = "Search students by name", description = "Returns paginated list of students whose name contains the search term")
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
           @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
   })
   public PageResponse<StudentResponse> searchStudents(
           @Parameter(description = "Name to search for")
           @RequestParam String name,
           @Parameter(description = "Page number")
           @RequestParam(defaultValue = "0") int page,
           @Parameter(description = "Page size")
           @RequestParam(defaultValue = "10") int size,
           @Parameter(description = "Sort by field")
           @RequestParam(defaultValue = "name") String sortBy,
           @Parameter(description = "Sort direction")
           @RequestParam(defaultValue = "asc") String sortDir
   ) {
      return studentService.searchStudentsByName(name, page, size, sortBy, sortDir);
   }

   //---------------------------------------------
   //GET students older than (with pagination)
   //---------------------------------------------
   //GET /api/students/older-than?age=20?&page=0&size=5
   @GetMapping("/older-than")
   @Operation(summary = "Get students older than", description = "Returns paginated list of students older than the specified age")
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
           @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(hidden = true)))
   })
   public PageResponse<StudentResponse> getStudentsOlderThan(
           @Parameter(description = "Minimum age")
           @RequestParam int age,
           @Parameter(description = "Page number")
           @RequestParam(defaultValue = "0") int page,
           @Parameter(description = "Page size")
           @RequestParam(defaultValue = "10") int size
   ) {
      return studentService.getStudentsOlderThan(age, page, size);
   }

   // GET /api/student/id
   @GetMapping("/{id}")
   @Operation(summary = "Get student by ID", description = "Returns a single student by their ID")
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Student found"),
           @ApiResponse(responseCode = "404", description = "Student not found", content = @Content(schema = @Schema(hidden = true)))
   })
   public StudentResponse getStudentById(
           @Parameter(description = "Student ID", required = true, example = "1")
           @PathVariable Long id)
   {
      return studentService.getStudentById(id);
   }

   //POST /api/students
   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   @Operation(
           summary = "Create a new student",
           description = "Create a new student. ADMIN only"
   )
   @ApiResponses({
           @ApiResponse(
                   responseCode = "201",
                   description = "Student created successfully"
           ),
           @ApiResponse(
                   responseCode = "400",
                   description = "Validation failed"
           ),
           @ApiResponse(
                   responseCode = "403",
                   description = "Forbidden - ADMIN role required"
           )
   })
   public StudentResponse createStudent(@Valid @RequestBody StudentRequest request) {
      return studentService.createStudent(request);
   }

   //POST /api/students/1/address -> Add address to student
   @PostMapping("/{id}/address")
   @Operation(
           summary = "Add address to student",
           description = "Adds or updates the address for a student. ADMIN only")
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Address added successfully"),
           @ApiResponse(responseCode = "404", description = "Student not found"),
           @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
   })
   public Student addAddress(
           @Parameter(description = "Student ID")
           @PathVariable Long id,
           @Valid @RequestBody Address address) {
      return studentService.addAddressToStudent(id, address);
   }

   //PUT /api/students/1
   @PutMapping("/{id}")
   @Operation(summary = "Update student", description = "Updates an existing student. ADMIN only")
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Student updated successfully"),
           @ApiResponse(responseCode = "404", description = "Student not found"),
           @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
   })
   public StudentResponse updateStudent(
           @Parameter(description = "Student ID")
           @PathVariable Long id,
           @Valid @RequestBody StudentRequest request) {
      return studentService.updateStudent(id, request);
   }

   //DELETE /api/students/1
   @DeleteMapping("/{id}")
   @ResponseStatus(HttpStatus.NO_CONTENT)
   @Operation(summary = "Delete student", description = "Deletes a student by ID. ADMIN only")
   @ApiResponses({
           @ApiResponse(
                   responseCode = "204",
                   description = "Student deleted successfully"
           ),
           @ApiResponse(
                   responseCode = "404",
                   description = "Student not found"
           ),
           @ApiResponse(
                   responseCode = "403",
                   description = "Forbidden - ADMIN role required"
           )
   })
   public void deleteStudent(
           @Parameter(description = "Student ID")
           @PathVariable Long id) {
      studentService.deleteStudent(id);
   }
}
