package com.school.school_management.service;

import com.school.school_management.model.Course;
import com.school.school_management.dto.PageResponse;
import com.school.school_management.model.Teacher;
import com.school.school_management.repo.TeacherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class TeacherService {

   private final TeacherRepository teacherRepository;

   public TeacherService (TeacherRepository teacherRepository) {
      this.teacherRepository = teacherRepository;
   }

   //Get all teachers
   public PageResponse<Teacher> getAllTeachers(
           int pageNum,
           int pageSize,
           String sortBy,
           String sortDir) {

      //create sort object
      Sort sort = sortDir.equalsIgnoreCase("asc")
              ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

      Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

      Page<Teacher> page = teacherRepository.findAll(pageable);

      return buildPageResponse(page);
   }

   //HELPER: build page response
   public PageResponse<Teacher> buildPageResponse(Page<Teacher> page) {
      return new PageResponse<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isLast(),
        page.isFirst()
      );
   }

   //Get one teacher by ID
   public Teacher getTeacherById(Long id) {
      return teacherRepository.findById(id)
              .orElseThrow(() -> new RuntimeException(
                      "Teacher with ID " + id + " not found"
              ));
   }

   //Create a teacher
   public Teacher createTeacher(Teacher teacher) {
      //Check if teacher is already in the system
      if (teacherRepository.findByEmail(teacher.getEmail()).isPresent()) {
         throw new RuntimeException("Teacher with email " + teacher.getEmail() + " already exists");
      }
      return teacherRepository.save(teacher);
   }

   //Update teacher's info
   public Teacher updateTeacher(Teacher updatedTeacher, Long id) {
      Teacher existingTeacher = getTeacherById(id);
      existingTeacher.setName(updatedTeacher.getName());
      existingTeacher.setEmail(updatedTeacher.getEmail());
      existingTeacher.setSpecialization(updatedTeacher.getSpecialization());
      return teacherRepository.save(existingTeacher);
   }

   //Delete teacher
   public void deleteTeacher(Long id) {
      getTeacherById(id);
      teacherRepository.deleteById(id);
   }

   //Get all courses taught by teacher
   public Set<Course> getCoursesByTeacher(Long teacherId) {

      //check if teacher exists
      Teacher teacher = getTeacherById(teacherId);

      return teacher.getCourses();
   }
}
