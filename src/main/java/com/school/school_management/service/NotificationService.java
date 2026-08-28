package com.school.school_management.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

   //simulate sending an email (takes 5 seconds)
   @Async
   public void sendWelcomeEmail(String email, String name) {
      System.out.println("START sending email to " + email
              + " | Thread: " + Thread.currentThread().getName()
      );

      try {
         Thread.sleep(5000);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }

      System.out.println("DONE! Welcome email sent to " + name + " at " + email);
   }

   @Async
   public void sendEnrollmentNotification(String studentName, String courseName) {
      System.out.println("SENDING enrollment notification to " + studentName + " for " + courseName);

      try {
         Thread.sleep(3000);
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }

      System.out.println("Notification sent!");
   }
}
