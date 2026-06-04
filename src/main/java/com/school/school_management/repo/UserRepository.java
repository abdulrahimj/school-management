package com.school.school_management.repo;

import com.school.school_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   //find user by username (for login)
   Optional<User> findByUsername(String username);

   //check if username exists (for registration)
   boolean existsByUsername(String username);

   //check if email exists (for registration)
   boolean existsByEmail(String email);
}
