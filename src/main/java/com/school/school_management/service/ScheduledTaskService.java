package com.school.school_management.service;

import com.school.school_management.repo.CourseRepository;
import com.school.school_management.repo.StudentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ScheduledTaskService {

   private final StudentRepository studentRepository;
   private final CourseRepository courseRepository;
   private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

   public ScheduledTaskService(
           StudentRepository studentRepository,
           CourseRepository courseRepository) {
      this.studentRepository = studentRepository;
      this.courseRepository = courseRepository;
   }

   //runs every 30 seconds
   //count students in DB every 30 seconds
   @Scheduled(fixedRate = 30000)
   public void reportStudentCount() {
      long count = studentRepository.count();
      String time = LocalDateTime.now().format(formatter);
      System.out.println("At [" + time + "] Total students in DB: " + count);
   }

   //runs every day at 2am
   @Scheduled(cron = "0 0 2 * * ?")
   public void dailyCleanup() {
      String time = LocalDateTime.now().format(formatter);
      System.out.println("At [" + time + "] Running daily cleanup");
      //In real app, delete token, archive old records, clean up temp files
   }

   //counts total courses in DB
   @Scheduled(fixedRate = 60000)
   public void totalCoursesInDB() {
      long count = courseRepository.count();
      String time = LocalDateTime.now().format(formatter);
      System.out.println("At [" + time + "] Total courses in DB: " + count);
   }
}
