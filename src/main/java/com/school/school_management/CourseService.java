package com.school.school_management;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class CourseService {

   private final CourseRepository courseRepository;
   private final StudentRepository studentRepository;

   public CourseService(
           CourseRepository courseRepository,
           StudentRepository studentRepository) {
      this.courseRepository = courseRepository;
      this.studentRepository = studentRepository;
   }

   //Get all courses
   public List<Course> getAllCourses() {
      return courseRepository.findAll();
   }

   //Get course by ID
   public Course getCourseById(Long id) {
      return courseRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Course with ID " + id + " not found"));
   }

   //Create a new course
   public Course createCourse(Course course) {
      if (courseRepository.findByName(course.getName()).isPresent()) {
         throw new RuntimeException("Course " + course.getName() + " already exits");
      }
      return courseRepository.save(course);
   }

   //Enroll student in course
   public Student enrollStudentInCourse(Long studentId, Long courseId) {

      //Find student
      Student student = studentRepository.findById(studentId)
              .orElseThrow(() -> new RuntimeException(
                      "Student with ID " + studentId + " not found"
              ));

      //Find course
      Course course = getCourseById(courseId);

      //Check if already enrolled
      if (student.getCourse().contains(course)) {
         throw new RuntimeException(
                 student.getName() + " is already enrolled in " + course.getName()
         );
      }

      //Add course to student's courses
      student.getCourse().add(course);

      //Add student to course's students (both sides)
      course.getStudents().add(student);

      //Save (cascade handle the join table)
      return studentRepository.save(student);
   }

   //Remove student from course
   public Student unenrollStudentFromCourse(Long studentId, Long courseId) {

      Student student = studentRepository.findById(studentId)
              .orElseThrow(() -> new RuntimeException(
                      "Student with ID " + studentId + " not found"
              ));

      Course course = getCourseById(courseId);

      //Remove course from student
      student.getCourse().remove(course);

      //Remove student from course
      course.getStudents().remove(student);

      return studentRepository.save(student);
   }

   //Get all students in a course
   public Set<Student> getStudentsInCourse(Long courseId) {
      Course course = getCourseById(courseId);
      return course.getStudents();
   }

   //Delete course
   public void deleteCourse(Long courseId) {
      getCourseById(courseId);
      courseRepository.deleteById(courseId);
   }
}
