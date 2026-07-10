package com.school.school_management.service;

import com.school.school_management.model.Course;
import com.school.school_management.repo.CourseRepository;
import com.school.school_management.model.Student;
import com.school.school_management.repo.StudentRepository;
import com.school.school_management.model.Teacher;
import com.school.school_management.repo.TeacherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CourseService {

   private final CourseRepository courseRepository;
   private final StudentRepository studentRepository;
   private final TeacherRepository teacherRepository;

   public CourseService(
           CourseRepository courseRepository,
           StudentRepository studentRepository,
           TeacherRepository teacherRepository) {
      this.courseRepository = courseRepository;
      this.studentRepository = studentRepository;
      this.teacherRepository = teacherRepository;
   }

   //Get all courses with pagination and sorting
   public PageResponse<Course> getAllCourses(
           int pageNum,
           int size,
           String sortBy,
           String sortDir) {

      //create sort object
      Sort sort = sortDir.equalsIgnoreCase("asc")
              ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

      //create pageable object
      Pageable pageable = PageRequest.of(pageNum, size, sort);

      Page<Course> page = courseRepository.findAll(pageable);

      return buildPageResponse(page);
   }

   public PageResponse<Course> searchCoursesByName(
           String name,
           int pageNum,
           int pageSize,
           String sortBy,
           String sortDir) {

      //create sort object
      Sort sort = sortDir.equalsIgnoreCase("asc")
              ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

      //create pageable object
      Pageable pageable = PageRequest.of(pageNum, pageSize, sort);

      Page<Course> page = courseRepository.findByNameContaining(name, pageable);

      return buildPageResponse(page);
   }

   //HELPER: build pageResponse
   public PageResponse<Course> buildPageResponse(Page<Course> page) {
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

   //Get course by ID
   public Course getCourseById(Long id) {
      return courseRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Course with ID " + id + " not found"));
   }

   //Create a new course
   public Course createCourse(Course course) {
      if (courseRepository.findByName(course.getName()).isPresent()) {
         throw new RuntimeException("Course " + course.getName() + " already exists");
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
      Course course = getCourseById(courseId);

      for (Student student : course.getStudents()) {
         student.getCourse().remove(course);
      }

      course.getStudents().clear();
      courseRepository.deleteById(courseId);
   }

   //Assign teacher to course
   public Course assignTeacherToCourse(Long courseId, Long teacherId) {
      //find course
      Course course = getCourseById(courseId);

      //find teacher
      Teacher teacher = teacherRepository.findById(teacherId)
              .orElseThrow(() -> new RuntimeException(
                      "Teacher with ID " + teacherId + " not found"
              ));

      //assign teacher to course
      course.setTeacher(teacher);

      //save course
      return courseRepository.save(course);
   }
}
