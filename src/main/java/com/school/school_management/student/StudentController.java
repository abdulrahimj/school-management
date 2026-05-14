package com.school.school_management.student;

import com.school.school_management.address.Address;
import com.school.school_management.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
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
   public PageResponse<Student> getAllStudents(
           @RequestParam(defaultValue = "0") int page,
           //which page? Default = 0 (first page)

           @RequestParam(defaultValue = "10") int size,
           //how many per page? Default = 10

           @RequestParam(defaultValue = "id") String sortBy,
           //sort by which field? Default = "id"

           @RequestParam(defaultValue = "asc") String sortDir
           //direction? Default = "asc" (ascending)
   ) {
      return studentService.getAllStudents(page, size, sortBy, sortDir);
   }

   //-----------------------------------------------
   // Search by name (with pagination)
   //----------------------------------------------
   //GET /api/students/search?name=ali&page=0&size=5
   @GetMapping("/search")
   public PageResponse<Student> searchStudents(
           @RequestParam String name,
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "10") int size,
           @RequestParam(defaultValue = "name") String sortBy,
           @RequestParam(defaultValue = "asc") String sortDir
   ) {
      return studentService.searchStudentsByName(name, page, size, sortBy, sortDir);
   }

   //---------------------------------------------
   //GET students older than (with pagination)
   //---------------------------------------------
   //GET /api/students/older-than?age=20?&page=0&size=5
   @GetMapping("/older-than")
   public PageResponse<Student> getStudentsOlderThan(
           @RequestParam int age,
           @RequestParam(defaultValue = "0") int page,
           @RequestParam(defaultValue = "10") int size
   ) {
      return studentService.getStudentsOlderThan(age, page, size);
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
