package com.jatin.resume_builder.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.jatin.resume_builder.document.Resume;
import com.jatin.resume_builder.document.User;
import com.jatin.resume_builder.dto.CreateResumeRequest;
import com.jatin.resume_builder.service.FileUploadService;
import com.jatin.resume_builder.service.ResumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/resumes")
@Slf4j
@Tag(name = "Resume Controller", description = "Handling Resume creation, updation, deletion")
public class ResumeController {

	private final ResumeService resumeService;
	private final FileUploadService fileUploadService;

	private String extractAuthenticatedEmail(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof User user && user.getEmail() != null && !user.getEmail().isBlank()) {
			return user.getEmail();
		}

		String authName = authentication.getName();
		if (authName != null && !authName.isBlank() && !"anonymousUser".equalsIgnoreCase(authName)) {
			return authName;
		}

		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user email is missing");
	}

	private User extractAuthenticatedUser(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof User user) {
			return user;
		}

		throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication context");
	}
	
	@PostMapping
	@Operation(summary = "Create Resume")
	public ResponseEntity<?> createResume(@Valid @RequestBody CreateResumeRequest request, Authentication authentication){
		User user = extractAuthenticatedUser(authentication);
		log.info("Creating resume for userId={}", user.getId());
		Resume resume = resumeService.createResume(request, user);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(resume);
	}

	@GetMapping("/new")
	@Operation(summary = "Create and return draft resume")
	public ResponseEntity<?> createDraftResume(Authentication authentication) {
		String userEmail = extractAuthenticatedEmail(authentication);
		log.info("Creating draft resume for userEmail={}", userEmail);
		Resume draft = resumeService.createDraftResume(userEmail);
		return ResponseEntity.status(HttpStatus.CREATED).body(draft);
	}
	
	@GetMapping
	@Operation(summary = "Get All Resumes")
	public ResponseEntity<?> getUserResumes(Authentication authentication){
		User user = extractAuthenticatedUser(authentication);
		List<Resume> resumes = resumeService.getUserResumes(user);
		
		return ResponseEntity.ok(resumes);
	}

	@GetMapping("/{id:[a-fA-F0-9]{24}}")
	@Operation(summary = "Get Resume by UserId")
	public ResponseEntity<?> getResumeById(@PathVariable String id, Authentication authentication){
		User user = extractAuthenticatedUser(authentication);
		log.info("Fetching resumeId={} for userId={}", id, user.getId());
		Resume resume = resumeService.getResumeById(id, user);
		
		return ResponseEntity.ok(resume);
	} 
	
	@PutMapping("/{id}")
	@Operation(summary = "Update Resume")
	public ResponseEntity<?> updateResume(@PathVariable String id, @RequestBody Resume updatedResume, Authentication authentication){
		User user = extractAuthenticatedUser(authentication);
		Resume resume = resumeService.updateResume(id, updatedResume, user);
		
		return ResponseEntity.ok(resume);
	}

	@PutMapping("/{id}/upload-images")
	@Operation(summary = "Upload images on Cloudinary")
	public ResponseEntity<?> uploadResumeImages(@PathVariable String id, 
												@RequestPart(value="thumbnail", required=true) MultipartFile thumbnail,
												@RequestPart(value="profileImage", required=false) MultipartFile profileImage,
												Authentication authentication) throws IOException{
	
		User user = extractAuthenticatedUser(authentication);
		// call file service method
		Map<String, String> response = fileUploadService.uploadResumeImages(id, user, thumbnail, profileImage);
		
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete Resume")
	public ResponseEntity<?> deleteResume(@PathVariable String id, Authentication authentication){
		User user = extractAuthenticatedUser(authentication);
		resumeService.deleteResume(id, user);
		
		return ResponseEntity.ok(Map.of("message", "Resume deleted Successfully"));
	}
}
