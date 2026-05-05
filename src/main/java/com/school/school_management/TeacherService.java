package com.school.school_management;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {

   private final TeacherRepository teacherRepository;
   private final CourseRepository courseRepository;

   public TeacherService (TeacherRepository teacherRepository, CourseRepository courseRepository) {
      this.teacherRepository = teacherRepository;
      this.courseRepository = courseRepository;
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
      existingTeacher.setSpecification(updatedTeacher.getSpecification());
      return teacherRepository.save(existingTeacher);
   }

   //Delete teacher
   public void deleteTeacher(Long id) {
      getTeacherById(id);
      teacherRepository.deleteById(id);
   }

   //Assign teacher to course
   public Course assignTeacherToCourse(Long teacherId, Long courseId) {
      //Find teacher
      Teacher teacher = getTeacherById(teacherId);

      //Find course
      Course course = courseRepository.findById(courseId)
              .orElseThrow(() -> new RuntimeException(
                      "Course ID " + courseId + " not found"
              ));

      //Check if teacher is already assigned to course
      if (teacher.getCourses().contains(course)) {
         throw new RuntimeException(teacher.getName() + " is already assigned to " + course.getName());
      }

      //Assign teacher to a course
      course.setTeacher(teacher);

      //Add course to teacher's courses
      teacher.getCourses().add(course);

      //Save courses
      return courseRepository.save(course);
   }
}
