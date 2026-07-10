package com.school.school_management;

import com.school.school_management.model.Teacher;
import com.school.school_management.repo.TeacherRepository;
import com.school.school_management.service.TeacherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

   @Mock
   private TeacherRepository teacherRepository;

   @InjectMocks
   private TeacherService teacherService;

   //Test data
   private Teacher drSmith;
   private Teacher profJohnson;

   @BeforeEach
   void setUp() {
      drSmith = new Teacher("Dr. Smith", "smith@school.com", "Mathematics");
      drSmith.setId(1L);

      profJohnson = new Teacher("Prof. Johnson", "johnson@school.com", "Science");
      profJohnson.setId(2L);
   }
}
