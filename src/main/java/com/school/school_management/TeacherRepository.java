package com.school.school_management;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

   Optional<Teacher> findByEmail(String Email);
}
