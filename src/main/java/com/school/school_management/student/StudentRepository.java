package com.school.school_management.student;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
   Optional<Student> findByEmail(String Email);

   //Paginated version of findAll
   //spring data Jpa handles everything
   Page<Student> findAll(Pageable pageable);

   //paginated search by name
   Page<Student> findByNameContaining(String name, Pageable pageable);

   //paginated search by age greater than
   Page<Student> findByAgeGreaterThan(int age, Pageable pageable);

}
