package com.school.school_management.service;

import com.school.school_management.dto.request.CourseRequest;
import com.school.school_management.dto.response.CourseResponse;
import com.school.school_management.dto.response.PageResponse;
import com.school.school_management.dto.response.StudentResponse;
import com.school.school_management.model.Course;
import com.school.school_management.repo.CourseRepository;
import com.school.school_management.model.Student;
import com.school.school_management.repo.StudentRepository;
import com.school.school_management.model.Teacher;
import com.school.school_management.repo.TeacherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
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
   public PageResponse<CourseResponse> getAllCourses(
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

   public PageResponse<CourseResponse> searchCoursesByName(
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
   public PageResponse<CourseResponse> buildPageResponse(Page<Course> page) {
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

   //Private Helper - find course entity or throw exception
   private Course findCourseById(Long id) {
      return courseRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Course with ID " + id + " not found"));
   }

   //Get course by ID
   @Cacheable(value = "courses", key = "#id")
   public CourseResponse getCourseById(Long id) {
      log.info("FINDING course {} - cache created", id);
      return mapToResponse(findCourseById(id));
   }

   //Create a new course
   @CacheEvict(value = "courses", allEntries = true)
   public CourseResponse createCourse(CourseRequest request) {
      if (courseRepository.findByName(request.name()).isPresent()) {
         throw new RuntimeException("Course " + request.name() + " already exists");
      }
      log.info("CREATING course - cache cleared");
      Course course = new Course(request.name(), request.description());
      Course saved = courseRepository.save(course);
      return mapToResponse(saved);
   }

   //Enroll student in course
   public Student enrollStudentInCourse(Long studentId, Long courseId) {

      //Find student
      Student student = studentRepository.findById(studentId)
              .orElseThrow(() -> new RuntimeException(
                      "Student with ID " + studentId + " not found"
              ));

      //Find course
      Course course = findCourseById(courseId);

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

      Course course = findCourseById(courseId);

      //Remove course from student
      student.getCourse().remove(course);

      //Remove student from course
      course.getStudents().remove(student);

      return studentRepository.save(student);
   }

   //Get all students in a course
   public Set<StudentResponse> getStudentsInCourse(Long courseId) {
      Course course = findCourseById(courseId);
      return course.getStudents()
              .stream()
              .map(this::mapToStudentResponse)
              .collect(Collectors.toSet());
   }

   //Delete course
   @CacheEvict(value = "courses", key = "#id")
   public void deleteCourse(Long courseId) {
      Course course = findCourseById(courseId);

      log.info("DELETING course {} - cache cleared", courseId);

      for (Student student : course.getStudents()) {
         student.getCourse().remove(course);
      }

      course.getStudents().clear();
      courseRepository.deleteById(courseId);
   }

   //Assign teacher to course
   @CacheEvict(value = "courses", key = "#courseId")
   public CourseResponse assignTeacherToCourse(Long courseId, Long teacherId) {
      //find course
      Course course = findCourseById(courseId);

      //find teacher
      Teacher teacher = teacherRepository.findById(teacherId)
              .orElseThrow(() -> new RuntimeException(
                      "Teacher with ID " + teacherId + " not found"
              ));

      log.info("ASSIGNING teacher {} to course {} - cache cleared", teacherId, courseId);

      //assign teacher to course
      course.setTeacher(teacher);

      //save course
      return mapToResponse(courseRepository.save(course));
   }

   //Convert Course entity to CourseResponse DTO
   private CourseResponse mapToResponse(Course course) {
      return new CourseResponse(
              course.getId(),
              course.getName(),
              course.getDescription(),
              course.getTeacher() != null ? course.getTeacher().getName() : null
      );
   }

   //Convert Student entity to StudentResponse DTO
   private StudentResponse mapToStudentResponse(Student student) {
      return new StudentResponse(
              student.getId(),
              student.getName(),
              student.getEmail(),
              student.getAge()
      );
   }
}
