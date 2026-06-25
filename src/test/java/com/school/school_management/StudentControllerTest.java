package com.school.school_management;

import com.school.school_management.model.Student;
import com.school.school_management.repo.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest //Load the full spring context
@AutoConfigureMockMvc //set up MockMvc automatically
@ActiveProfiles("test") //use H2 test database
public class StudentControllerTest {

   @Autowired
   private ObjectMapper objectMapper;
   //simulates HTTP requests (like postman in code)

   @Autowired
   private StudentRepository studentRepository;

   private Student alice;

   @BeforeEach
   void setUp() {
      studentRepository.deleteAll();

      alice = studentRepository.save(
              new Student("Alice", "alice@gmail.com", 20)
      );
   }
}
