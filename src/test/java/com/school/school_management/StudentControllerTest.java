package com.school.school_management;

import com.school.school_management.model.Student;
import com.school.school_management.repo.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest //Load the full spring context
@AutoConfigureMockMvc //set up MockMvc automatically
@ActiveProfiles("test") //use H2 test database
public class StudentControllerTest {

   @Autowired
   private ObjectMapper objectMapper;

   @Autowired
   private MockMvc mockMvc;

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

   //TEST: GET all students (authenticated)
   @Test
   @DisplayName("Should return all students when authenticated")
   @WithMockUser(username = "testuser", roles = {"USER"})
   //Pretend this request comes from a USER
   //No need to generate real jwt for this test
   void shouldReturnAllStudents() throws Exception {

      mockMvc.perform(
              get("/api/students")  //simulate: GET /api/students
      )
              .andExpect(status().isOk())
              //Expect: 200 OK

              .andExpect(jsonPath("$.content").isArray())
              //Expect: response.content is an Array

              .andExpect(jsonPath("$.content", hasSize(1)))
              //Expect: content has 1 student

              .andExpect(jsonPath("$.content[0].name")
                      .value("Alice"))
              //Expect: first student's name is Alice

              .andExpect(jsonPath("$.totalElements").value(1));
               //Expect: totalElements is 1
   }
}
