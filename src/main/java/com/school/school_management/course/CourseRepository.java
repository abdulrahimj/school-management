package com.school.school_management.course;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
   Optional<Course> findByName(String name);

   //pagination find all
   Page<Course> findAll(Pageable pageable);

   //pagination search by name
   Page<Course> findByNameContaining(String name, Pageable pageable);
}
