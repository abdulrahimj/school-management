package com.school.school_management;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

   private final StudentService studentService;

   public StudentController(StudentService studentService) {
      this.studentService = studentService;
   }

   //GET /api/students
   @GetMapping
   public List<Student> getAllStudents() {
      return studentService.getAllStudents();
   }

   // GET /api/student/id
   @GetMapping("/{id}")
   public Student getStudentById(@PathVariable Long id) {
      return studentService.getStudentById(id);
   }

   //POST /api/students
   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Student createStudent(@RequestBody Student student) {

      return studentService.createStudent(student);
   }

   //POST /api/students/1/address -> Add address to student
   @PostMapping("/{id}/address")
   public Student addAddress(
           @PathVariable Long id,
           @Valid @RequestBody Address address) {
      return studentService.addAddressToStudent(id, address);
   }

   //PUT /api/students/1
   @PutMapping("/{id}")
   public Student updateStudent(
           @PathVariable Long id,
           @Valid @RequestBody Student student) {
      return studentService.updateStudent(id, student);
   }

   //DELETE /api/students/1
   @DeleteMapping("/{id}")
   @ResponseStatus(HttpStatus.NO_CONTENT)
   public void deleteStudent(@PathVariable Long id) {
      studentService.deleteStudent(id);
   }
}
