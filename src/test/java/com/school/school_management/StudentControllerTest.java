package com.school.school_management;

import com.school.school_management.model.Student;
import com.school.school_management.repo.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

   //TEST: GET student by ID - FOUND
   @Test
   @DisplayName("Should return student by ID")
   @WithMockUser(roles = {"USER"})
   void shouldReturnStudentById() throws Exception {

      mockMvc.perform(get("/api/students/", alice.getId()))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.name").value("Alice"))
              .andExpect(jsonPath("$.email")
                      .value("alice@gmail.com"))
              .andExpect(jsonPath("$.age").value(20));
   }

   //TEST: GET student by ID - NOT FOUND
   @Test
   @DisplayName("Should return 404 when student not found")
   @WithMockUser(roles = {"USER"})
   void shouldReturn404WhenStudentNotFound() throws Exception {

      mockMvc.perform(get("/api/students/{id}", 999L))
              .andExpect(status().isNotFound());
   }

   //TEST: POST create student - ADMIN SUCCESS
   @Test
   @DisplayName("Should create student when ADMIN")
   @WithMockUser(roles = {"ADMIN"}) //admin can create
   void shouldCreateStudentWhenAdmin() throws Exception {

      Student newStudent = new Student("Bob", "bob@gmail.com", 22);

      mockMvc.perform(
              post("/api/students")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(newStudent)) //convert student object to json string
      )
              .andExpect(status().isCreated())
              .andExpect(jsonPath("$.name").value("Bob"))
              .andExpect(jsonPath("$.email").value("bob@gmail.com"))
              .andExpect(jsonPath("$.id").isNotEmpty());
   }

   // ─────────────────────────────────────────────
   // TEST: POST create student - USER FORBIDDEN
   // ─────────────────────────────────────────────
   @Test
   @DisplayName("Should return 403 when USER tries to create")
   @WithMockUser(roles = {"USER"})
   // ↑ USER cannot create!
   void shouldReturn403WhenUserTriesToCreate() throws Exception {

      Student newStudent = new Student(
              "Bob", "bob@email.com", 22
      );

      mockMvc.perform(
                      post("/api/students")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(newStudent))
              )
              .andExpect(status().isForbidden());
      // ↑ Expect: 403 Forbidden
   }

   // ─────────────────────────────────────────────
   // TEST: POST create student - VALIDATION FAILS
   // ─────────────────────────────────────────────
   @Test
   @DisplayName("Should return 400 when validation fails")
   @WithMockUser(roles = {"ADMIN"})
   void shouldReturn400WhenValidationFails() throws Exception {

      Student invalidStudent = new Student(
              "",          // ← Empty name! Fails @NotBlank
              "not-email", // ← Invalid email! Fails @Email
              -5           // ← Negative age! Fails @Min
      );

      mockMvc.perform(
                      post("/api/students")
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper.writeValueAsString(invalidStudent))
              )
              .andExpect(status().isBadRequest())
              // ↑ Expect: 400 Bad Request
              .andExpect(jsonPath("$.errors").exists());
      // ↑ Expect: errors field in response
   }

   // ─────────────────────────────────────────────
   // TEST: DELETE - ADMIN SUCCESS
   // ─────────────────────────────────────────────
   @Test
   @DisplayName("Should delete student when ADMIN")
   @WithMockUser(roles = {"ADMIN"})
   void shouldDeleteStudentWhenAdmin() throws Exception {

      mockMvc.perform(
                      delete("/api/students/{id}", alice.getId())
              )
              .andExpect(status().isNoContent());
      // ↑ Expect: 204 No Content

      // Verify actually deleted from database
      mockMvc.perform(
                      get("/api/students/{id}", alice.getId())
              )
              .andExpect(status().isNotFound());
      // ↑ Should be gone now!
   }

   // ─────────────────────────────────────────────
   // TEST: PUT update student
   // ─────────────────────────────────────────────
   @Test
   @DisplayName("Should update student when ADMIN")
   @WithMockUser(roles = {"ADMIN"})
   void shouldUpdateStudentWhenAdmin() throws Exception {

      Student updatedData = new Student(
              "Alice Updated",
              "alice.updated@email.com",
              21
      );

      mockMvc.perform(
                      put("/api/students/{id}", alice.getId())
                              .contentType(MediaType.APPLICATION_JSON)
                              .content(objectMapper
                                      .writeValueAsString(updatedData))
              )
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.name")
                      .value("Alice Updated"))
              .andExpect(jsonPath("$.age").value(21));
   }
}
