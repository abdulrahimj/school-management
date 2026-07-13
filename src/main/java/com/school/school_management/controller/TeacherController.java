package com.school.school_management.controller;

import com.school.school_management.dto.request.TeacherRequest;
import com.school.school_management.dto.response.CourseResponse;
import com.school.school_management.dto.response.PageResponse;
import com.school.school_management.dto.response.TeacherResponse;
import com.school.school_management.service.TeacherService;
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
@RequestMapping("/api/teachers")
@Tag(
        name = "Teachers",
        description = "Endpoints for managing teachers"
)
@SecurityRequirement(name = "bearerAuth")  //all endpoints in this controller need jwt token. shows lock icon in swagger UI
public class TeacherController {

   private final TeacherService teacherService;

   public TeacherController(TeacherService teacherService) {
      this.teacherService = teacherService;
   }

   //GET /api/teachers
   @GetMapping
   @Operation(
           summary = "Get all teachers",
           description = "Returns paginated list of all teachers. Requires authentication."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Teachers retrieved successfully"),
           @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required", content = @Content(schema = @Schema(hidden = true)))
   })
   public PageResponse<TeacherResponse> getAllTeachers(
           @Parameter(description = "Page number start at 0")
           @RequestParam(defaultValue = "0") int page,

           @Parameter(description = "Number of teachers per page")
           @RequestParam(defaultValue = "5") int size,

           @Parameter(description = "Field to sort by")
           @RequestParam(defaultValue = "id") String sortBy,

           @Parameter(description = "Sort direction: asc or desc")
           @RequestParam(defaultValue = "asc") String sortDir) {
      return teacherService.getAllTeachers(page, size, sortBy, sortDir);
   }

   //GET /api/teachers/id
   @GetMapping("/{id}")
   @Operation(
           summary = "Get teacher by ID",
           description = "Return a single teacher by their ID"
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Teacher found"),
           @ApiResponse(responseCode = "404", description = "Teacher not found", content = @Content(schema = @Schema(hidden = true)))
   })
   public TeacherResponse getTeacherById(
           @Parameter(description = "Teacher ID", required = true, example = "1")
           @PathVariable Long id) {
      return teacherService.getTeacherById(id);
   }

   //POST /api/teachers
   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   @Operation(
           summary = "Create a new teacher",
           description = "Only ADMIN can create a new teacher."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "201", description = "Teacher created successfully"),
           @ApiResponse(responseCode = "400", description = "Validation failed"),
           @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
   })
   public TeacherResponse createTeacher(@Valid @RequestBody TeacherRequest request) {
      return teacherService.createTeacher(request);
   }

   //PUT /api/teachers/id
   @PutMapping("/{id}")
   @Operation(
           summary = "Update a teacher",
           description = "Only ADMIN can update an existing teacher."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Teacher updated successfully"),
           @ApiResponse(responseCode = "400", description = "Validation failed"),
           @ApiResponse(responseCode = "404", description = "Teacher not found", content = @Content(schema = @Schema(hidden = true))),
           @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
   })
   public TeacherResponse updateTeacher(
           @Valid @RequestBody TeacherRequest request,
           @Parameter(description = "Teacher ID", required = true, example = "1")
           @PathVariable Long id) {

      return teacherService.updateTeacher(request, id);
   }

   //DELETE /api/teachers/id
   @DeleteMapping("/{id}")
   @ResponseStatus(HttpStatus.NO_CONTENT)
   @Operation(
           summary = "Delete a teacher",
           description = "Only ADMIN can delete a teacher."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "204", description = "Teacher deleted successfully"),
           @ApiResponse(responseCode = "404", description = "Teacher not found", content = @Content(schema = @Schema(hidden = true))),
           @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
   })
   public void deleteTeacher(
           @Parameter(description = "Teacher ID", required = true, example = "1")
           @PathVariable Long id) {
      teacherService.deleteTeacher(id);
   }

   //GET /api/teachers/1/courses
   @GetMapping("/{teacherId}/courses")
   @Operation(
           summary = "Get all courses taught by a teacher",
           description = "Returns the set of courses assigned to the given teacher. Requires authentication."
   )
   @ApiResponses({
           @ApiResponse(responseCode = "200", description = "Courses retrieved successfully"),
           @ApiResponse(responseCode = "404", description = "Teacher not found", content = @Content(schema = @Schema(hidden = true))),
           @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required", content = @Content(schema = @Schema(hidden = true)))
   })
   public Set<CourseResponse> getCoursesByTeacher(
           @Parameter(description = "Teacher ID", required = true, example = "1")
           @PathVariable Long teacherId) {
      return teacherService.getCoursesByTeacher(teacherId);
   }
}
