package com.school.school_management;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class TeacherService {

   private final TeacherRepository teacherRepository;

   public TeacherService (TeacherRepository teacherRepository) {
      this.teacherRepository = teacherRepository;
   }

   //Get all teachers
   public List<Teacher> getAllTeachers() {
      return teacherRepository.findAll();
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
