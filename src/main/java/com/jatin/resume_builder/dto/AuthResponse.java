package com.jatin.resume_builder.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

	private String id;
	private String name;
	private String email;
	private String profileImageUrl;
	private String subscriptionPlan;
	private Boolean emailVerified;
	private String token;
	private Instant createdAt;
	private Instant updatedAt;
}
