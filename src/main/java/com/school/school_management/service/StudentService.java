package com.school.school_management.service;

import com.school.school_management.dto.request.StudentRequest;
import com.school.school_management.dto.response.PageResponse;
import com.school.school_management.dto.response.StudentResponse;
import com.school.school_management.model.Address;
import com.school.school_management.model.Student;
import com.school.school_management.repo.StudentRepository;
import jakarta.validation.Valid;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Slf4j
@Service
public class StudentService {

   private final StudentRepository studentRepository;

   public StudentService(StudentRepository studentRepository) {
      this.studentRepository = studentRepository;
   }

   //Get all students (with pagination + sorting)
   public PageResponse<StudentResponse> getAllStudents(
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

      //convert each student entity to studentResponseDTO
      List<StudentResponse> content = page.getContent()
              .stream()
              .map(this::mapToResponse)
              .toList();

      //build our clean response
      return new PageResponse<>(
              content,
              page.getNumber(),
              page.getSize(),
              page.getTotalElements(),
              page.getTotalPages(),
              page.isLast(),
              page.isFirst()
      );
   }
   
   //search students by name (with pagination)
   public PageResponse<StudentResponse> searchStudentsByName(
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
   public PageResponse<StudentResponse> getStudentsOlderThan(
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
   private PageResponse<StudentResponse> buildPageResponse(Page<Student> page) {
      
      return new PageResponse<>(
              page.getContent().stream().map(this::mapToResponse).toList(), //list of students
              page.getNumber(), //current page number
              page.getSize(), //page size
              page.getTotalElements(), //total students in DB
              page.getTotalPages(), //total pages
              page.isLast(), //is this last page?
              page.isFirst() //is this first page?
      );
   }

   //Private Helper - Find student or throw exception
   private Student findStudentById(Long id) {
      return studentRepository.findById(id)
              .orElseThrow(() -> new RuntimeException(
                      "Student with ID " + id + " not found"
              ));
   }

   @Cacheable(value = "students", key = "#id")
   public StudentResponse getStudentById(Long id) {
      log.info("DATABASE HIT: Finding student {}", id);
      Student student = findStudentById(id);
      return mapToResponse(student);
   }

   public StudentResponse createStudent(StudentRequest request) {
      //Check duplicate email
      if (studentRepository.findByEmail(request.email()).isPresent()) {
         throw new RuntimeException(
                 "Email " + request.email() + " already exists"
         );
      }
      Student student = new Student(request.name(), request.email(), request.age());
      Student saved = studentRepository.save(student);
      return mapToResponse(saved);
   }

   //Add address to existing student
   public Student addAddressToStudent(Long studentId, Address address) {
      //find the student
      Student student = findStudentById(studentId);

      //Link address to student
      address.setStudent(student);

      //Link student to address
      student.setAddress(address);

      //save student (cascade saves address too)
      return studentRepository.save(student);
   }

   @CacheEvict(value = "students", key = "#id")
   public StudentResponse updateStudent(Long id, StudentRequest request) {

      log.info("UPDATING student {} - cache cleared!", id);

      Student existing = findStudentById(id);
      existing.setName(request.name());
      existing.setEmail(request.email());
      existing.setAge(request.age());
      Student updatedStudent = studentRepository.save(existing);
      return mapToResponse(updatedStudent);
   }

   public void deleteStudent(Long id) {
      findStudentById(id);
      studentRepository.deleteById(id);
   }

   //Convert entity to Response DTO
   private StudentResponse mapToResponse(Student student) {
      return new StudentResponse(
              student.getId(),
              student.getName(),
              student.getEmail(),
              student.getAge()
      );
   }
}
