package com.school.school_management.teacher;

import com.school.school_management.course.Course;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

   private final TeacherService teacherService;

   public TeacherController(TeacherService teacherService) {
      this.teacherService = teacherService;
   }

   //GET /api/teachers
   @GetMapping
   public List<Teacher> getAllTeachers() {
      return teacherService.getAllTeachers();
   }

   //GET /api/teachers/id
   @GetMapping("/{id}")
   public Teacher getTeacherById(@PathVariable Long id) {
      return teacherService.getTeacherById(id);
   }

   //POST /api/teachers
   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Teacher createTeacher(@Valid @RequestBody Teacher teacher) {
      return teacherService.createTeacher(teacher);
   }

   //PUT /api/teachers/id
   @PutMapping("/{id}")
   public Teacher updateTeacher(
           @Valid @RequestBody Teacher updatedTeacher,
           @PathVariable Long id) {

      return teacherService.updateTeacher(updatedTeacher, id);
   }

   //DELETE /api/teachers/id
   @DeleteMapping("/{id}")
   @ResponseStatus(HttpStatus.NO_CONTENT)
   public void deleteTeacher(@PathVariable Long id) {
      teacherService.deleteTeacher(id);
   }

   //GET /api/teachers/1/courses
   @GetMapping("/{teacherId}/courses")
   public Set<Course> getCoursesByTeacher(@PathVariable Long teacherId) {
      return teacherService.getCoursesByTeacher(teacherId);
   }
}
