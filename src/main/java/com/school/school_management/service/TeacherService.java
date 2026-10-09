package com.school.school_management.service;

import com.school.school_management.dto.request.TeacherRequest;
import com.school.school_management.dto.response.CourseResponse;
import com.school.school_management.dto.response.PageResponse;
import com.school.school_management.dto.response.TeacherResponse;
import com.school.school_management.model.Course;
import com.school.school_management.model.Teacher;
import com.school.school_management.repo.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TeacherService {

   private final TeacherRepository teacherRepository;

   public TeacherService (TeacherRepository teacherRepository) {
      this.teacherRepository = teacherRepository;
   }

   //Get all teachers
   public PageResponse<TeacherResponse> getAllTeachers(
           int pageNum,
           int pageSize,
           String sortBy,
           String sortDir) {

      //create sort object
      Sort sort = sortDir.equalsIgnoreCase("asc")
              ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

      Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

      log.info("Fetching all teachers from database");

      Page<Teacher> page = teacherRepository.findAll(pageable);

      return buildPageResponse(page);
   }

   //HELPER: build page response
   public PageResponse<TeacherResponse> buildPageResponse(Page<Teacher> page) {
      return new PageResponse<>(
        page.getContent().stream().map(this::mapToResponse).toList(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isLast(),
        page.isFirst()
      );
   }

   //Private Helper - find teacher entity or throw exception
   private Teacher findTeacherById(Long id) {
      return teacherRepository.findById(id)
              .orElseThrow(() -> new RuntimeException(
                      "Teacher with ID " + id + " not found"
              ));
   }

   //Get one teacher by ID
   public TeacherResponse getTeacherById(Long id) {

      log.info("Fetching teacher with ID: {}", id);

      return mapToResponse(findTeacherById(id));
   }

   //Create a teacher
   public TeacherResponse createTeacher(TeacherRequest request) {

      log.info("Attempting to register teaching with email: {}", request.email());

      //Check if teacher is already in the system
      if (teacherRepository.findByEmail(request.email()).isPresent()) {
         log.warn("Teacher creation rejected: Email '{}' is already in use", request.email());
         throw new RuntimeException("Teacher with email " + request.email() + " already exists");
      }
      Teacher teacher = new Teacher(request.name(), request.email(), request.specialization());
      Teacher saved = teacherRepository.save(teacher);
      log.info("Teacher registered successfully with ID: {}", saved.getId());
      return mapToResponse(saved);
   }

   //Update teacher's info
   public TeacherResponse updateTeacher(TeacherRequest request, Long id) {
      Teacher existingTeacher = findTeacherById(id);
      existingTeacher.setName(request.name());
      existingTeacher.setEmail(request.email());
      existingTeacher.setSpecialization(request.specialization());
      return mapToResponse(teacherRepository.save(existingTeacher));
   }

   //Delete teacher
   public void deleteTeacher(Long id) {
      log.warn("Deleting teacher with ID: {}", id);
      findTeacherById(id);
      teacherRepository.deleteById(id);
      log.info("Teacher ID: {} deleted successfully", id);
   }

   //Get all courses taught by teacher
   public Set<CourseResponse> getCoursesByTeacher(Long teacherId) {

      log.info("Fetching assigned courses for teacher ID: {}", teacherId);

      //check if teacher exists
      Teacher teacher = findTeacherById(teacherId);

      return teacher.getCourses()
              .stream()
              .map(this::mapToCourseResponse)
              .collect(Collectors.toSet());
   }

   //Convert Teacher entity to TeacherResponse DTO
   private TeacherResponse mapToResponse(Teacher teacher) {
      return new TeacherResponse(
              teacher.getId(),
              teacher.getName(),
              teacher.getEmail(),
              teacher.getSpecialization(),
              teacher.getCourses() != null ? teacher.getCourses().size() : 0
      );
   }

   //Convert Course entity to CourseResponse DTO
   private CourseResponse mapToCourseResponse(Course course) {
      return new CourseResponse(
              course.getId(),
              course.getName(),
              course.getDescription(),
              course.getTeacher() != null ? course.getTeacher().getName() : null
      );
   }
}
