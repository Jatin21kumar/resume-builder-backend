package com.jatin.resume_builder.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jatin.resume_builder.document.User;

public interface UserRepository extends MongoRepository<User, String>{
	
	Optional<User> findByEmail(String email);
	
	Boolean existsByEmail(String email);
	
	Optional<User> findByVerificationToken(String token);
}
