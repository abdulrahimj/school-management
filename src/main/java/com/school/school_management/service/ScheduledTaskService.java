package com.school.school_management.service;

import com.school.school_management.repo.StudentRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ScheduledTaskService {

   private final StudentRepository studentRepository;
   private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

   public ScheduledTaskService(StudentRepository studentRepository) {
      this.studentRepository = studentRepository;
   }

   //runs every 10 seconds
   @Scheduled(fixedRate = 10000)
   public void heartbeat() {
      String time = LocalDateTime.now().format(formatter);
      System.out.println("Heart beats [" + time + "] App is alive (every 10 seconds)");
   }

   //runs every 30 seconds
   //count students in DB every 30 seconds
   @Scheduled(fixedRate = 30000)
   public void reportStudentCount() {
      long count = studentRepository.count();
      String time = LocalDateTime.now().format(formatter);
      System.out.println("At [" + time + "] Total students in DB: " + count);
   }
}
