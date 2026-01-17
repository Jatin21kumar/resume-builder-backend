package com.jatin.resume_builder.document;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection="users")
public class User {
	private String id;
	private String name;
	private String email;
	private String password;
	private String profileImageUrl;
	@Builder.Default
	private String subscritptionPlan = "basic";
	@Builder.Default
	private boolean emailVerified = false;
	private String verificationToken;
	private Instant verificationExpires;
	
	@CreatedDate
	private Instant createdAt;
	@LastModifiedDate
	private Instant updatedAt;
}
