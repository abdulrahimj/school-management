package com.school.school_management.service;

import com.school.school_management.model.User;
import com.school.school_management.repo.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

   private final UserRepository userRepository;

   public CustomUserDetailsService(UserRepository userRepository) {
      this.userRepository = userRepository;
   }

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

      //find user in database
      User user = userRepository.findByUsername(username)
              .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

      //convert our user to spring's UserDetails
      return new org.springframework.security.core.userdetails.User(
              user.getUsername(),
              user.getPassword(),
              List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
      );
      //spring security needs roles with "ROLE_" prefix
      //"ADMIN" becomes "ROLE_ADMIN", "USER" becomes "ROLE_USER"
   }
}
