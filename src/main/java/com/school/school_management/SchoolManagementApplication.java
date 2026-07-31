package com.school.school_management;

import com.school.school_management.model.Student;
import com.school.school_management.repo.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableCaching  //This activates caching
public class SchoolManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolManagementApplication.class, args);
	}

	// This runs ONCE when the app starts
	// Creates dummy data for testing!
	@Bean
	public CommandLineRunner loadData(StudentRepository studentRepository) {

		return args -> {

			// Only add data if database is empty
			if (studentRepository.count() == 0) {

				studentRepository.save(new Student("Alice Johnson","alice22@email.com", 25));
				studentRepository.save(new Student("Bob Smith","bob@email.com", 22));
				studentRepository.save(new Student("Charlie Brown","charlie@email.com", 19));
				studentRepository.save(new Student("Diana Prince","diana@email.com", 21));
				studentRepository.save(new Student("Edward King","edward@email.com", 23));
				studentRepository.save(new Student("Fiona Green","fiona@email.com", 20));
				studentRepository.save(new Student("George White","george@email.com", 24));
				studentRepository.save(new Student("Hannah Blue","hannah@email.com", 18));
				studentRepository.save(new Student("Ivan Black","ivan@email.com", 25));
				studentRepository.save(new Student("Julia Red","julia@email.com", 22));
				studentRepository.save(new Student("Kevin Gold","kevin@email.com", 21));
				studentRepository.save(new Student("Laura Silver","laura@email.com", 19));

				System.out.println("12 students added!");
			}
		};
	}
}
