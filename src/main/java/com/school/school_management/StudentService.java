package com.school.school_management;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

   private final StudentRepository studentRepository;

   public StudentService(StudentRepository studentRepository) {
      this.studentRepository = studentRepository;
   }

   public List<Student> getAllStudents() {
      return studentRepository.findAll();
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
