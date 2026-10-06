package com.school.school_management.config;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceHealthIndicator implements HealthIndicator {

   @Override
   public Health health() {
      boolean serviceIsRunning = checkNotificationService();

      if (serviceIsRunning) {
         return Health.up()
                 .withDetail("NotificationService", "Email provider is reachable")
                 .build();
      } else {
         return Health.down()
                 .withDetail("NotificationService", "Email provider is UNREACHABLE")
                 .build();
      }
   }

   private boolean checkNotificationService() {
      //simulated health ping (e.g., check connection to Mail server)
      return true;
   }
}
