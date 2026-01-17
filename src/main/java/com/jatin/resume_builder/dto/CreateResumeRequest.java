package com.jatin.resume_builder.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateResumeRequest {

	@NotBlank(message="Title Required")
	private String title;
}
