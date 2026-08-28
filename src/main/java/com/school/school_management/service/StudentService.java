package com.school.school_management.service;

import com.school.school_management.dto.request.StudentRequest;
import com.school.school_management.dto.response.PageResponse;
import com.school.school_management.dto.response.StudentResponse;
import com.school.school_management.model.Address;
import com.school.school_management.model.Student;
import com.school.school_management.repo.StudentRepository;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

@Service
public class StudentService {

   private static final Logger log = LoggerFactory.getLogger(StudentService.class);
   private final StudentRepository studentRepository;
   private final NotificationService notificationService;

   public StudentService(
           StudentRepository studentRepository,
           NotificationService notificationService) {

      this.studentRepository = studentRepository;
      this.notificationService = notificationService;
   }

   //Get all students (with pagination + sorting)
   public PageResponse<StudentResponse> getAllStudents(
           int pageNumber,
           int pageSize,
           String sortBy,
           String sortDirection) {

      log.info("Fetching students page: {}, size: {}, sortBy: {}", pageNumber, pageSize, sortBy);

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

   @Cacheable(value = "students", key = "#id")
   public StudentResponse getStudentById(Long id) {
      log.info("DATABASE HIT: Finding student {}", id);
      Student student = findStudentById(id);
      return mapToResponse(student);
   }

   //Clear DB and cache when a new student is created
   @CacheEvict(value = "students", allEntries = true)
   public StudentResponse createStudent(StudentRequest request) {

      log.info("CREATING student with email: {} - All cache cleared", request.email());

      //Check duplicate email
      if (studentRepository.findByEmail(request.email()).isPresent()) {
         log.warn("Student creation failed: email {} already exists", request.email());
         throw new RuntimeException(
                 "Email " + request.email() + " already exists"
         );
      }

      //create a new student
      Student student = new Student(
              request.name(),
              request.email(),
              request.age()
      );

      Student saved = studentRepository.save(student);
      log.info("Student saved to successfully with ID: {}", saved.getId());

      //send welcome email (Not async yet)
      notificationService.sendWelcomeEmail(
              request.email(),
              request.name()
      );

      log.info("Returning response to client");
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

      log.info("UPDATING student id: {} - cache cleared!", id);

      Student existing = findStudentById(id);
      existing.setName(request.name());
      existing.setEmail(request.email());
      existing.setAge(request.age());

      Student updatedStudent = studentRepository.save(existing);
      log.info("Student id: {} updated successfully", updatedStudent.getId());
      return mapToResponse(updatedStudent);
   }

   //delete student from DB and cache
   @CacheEvict(value = "students", key = "#id")
   public void deleteStudent(Long id) {
      log.info("DELETING student with id: {} - cache cleared", id);
      findStudentById(id);
      studentRepository.deleteById(id);
      log.info("Student with ID: {} deleted successfully", id);
   }

   // -------------------------------------------------
   //  PRIVATE HELPERS
   // -------------------------------------------------
   private Student findStudentById(Long id) {
      return studentRepository.findById(id)
              .orElseThrow(() -> {
                 log.error("Student lookup failed: ID {} not found", id);
                 return new RuntimeException("Student with ID " + id + " not found");
              });
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
