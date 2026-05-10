package com.school.school_management.student;

import com.school.school_management.address.Address;
import com.school.school_management.dto.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
public class StudentService {

   private final StudentRepository studentRepository;

   public StudentService(StudentRepository studentRepository) {
      this.studentRepository = studentRepository;
   }

   //Get all students (with pagination + sorting)
   public PageResponse<Student> getAllStudents(
           int pageNumber,
           int pageSize,
           String sortBy,
           String sortDirection) {

      //create sort object
      Sort sort = sortDirection.equalsIgnoreCase("asc")
              ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

      //create pageable object
      Pageable pageable = PageRequest.of(
              pageNumber, //which page (0, 1, 2...)
              pageSize, //how many per page
              sort // how to sort
      );

      //get page from repository
      Page<Student> page = studentRepository.findAll(pageable);

      //build our clean response
      return buildPageResponse(page);
   }
   
   //search students by name (with pagination)
   public PageResponse<Student> searchStudentsByName(
           String name,
           int pageNumber,
           int pageSize,
           String sortBy,
           String sortDirection) {
      
      Sort sort = sortDirection.equalsIgnoreCase("asc")
              ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
      
      Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
      
      Page<Student> page = studentRepository.findByNameContaining(name, pageable);
      
      return buildPageResponse(page);
   }
   
   //search students older than (with pagination)
   public PageResponse<Student> getStudentsOlderThan(
           int age,
           int pageNumber,
           int pageSize) {
      
      Pageable pageable = PageRequest.of(
              pageNumber,
              pageSize,
              Sort.by("age").ascending()
      );
      
      Page<Student> page = studentRepository.findByAgeGreaterThan(age, pageable);
      
      return buildPageResponse(page);
   }
   
   // -------------------------------------
   // helper: build PageResponse from page
   // ------------------------------------
   private PageResponse<Student> buildPageResponse(Page<Student> page) {
      
      return new PageResponse<>(
              page.getContent(), //list of students
              page.getNumber(), //current page number
              page.getSize(), //page size
              page.getTotalElements(), //total students in DB
              page.getTotalPages(), //total pages
              page.isLast(), //is this last page?
              page.isFirst() //is this first page?
      );
   }
   
   public Student getStudentById(Long id) {
      return studentRepository.findById(id)
              .orElseThrow(() -> new RuntimeException(
                      "Student with ID " + id + " not found"
              ));
   }

   public Student createStudent(Student student) {
      //Check duplicate email
      if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
         throw new RuntimeException(
                 "Email " + student.getEmail() + " already exists"
         );
      }
      return studentRepository.save(student);
   }

   //Add address to existing student
   public Student addAddressToStudent(Long studentId, Address address) {
      //find the student
      Student student = getStudentById(studentId);

      //Link address to student
      address.setStudent(student);

      //Link student to address
      student.setAddress(address);

      //save student (cascade saves address too)
      return studentRepository.save(student);
   }

   public Student updateStudent(Long id, Student updatedStudent) {
      Student existing = getStudentById(id);
      existing.setName(updatedStudent.getName());
      existing.setEmail(updatedStudent.getEmail());
      existing.setAge(updatedStudent.getAge());
      return studentRepository.save(existing);
   }

   public void deleteStudent(Long id) {
      getStudentById(id);
      studentRepository.deleteById(id);
   }
}
