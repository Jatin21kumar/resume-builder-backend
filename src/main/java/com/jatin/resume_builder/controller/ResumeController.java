package com.jatin.resume_builder.controller;

import java.io.IOException;
import java.security.DrbgParameters.Reseed;
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
import com.jatin.resume_builder.document.Resume;
import com.jatin.resume_builder.dto.CreateResumeRequest;
import com.jatin.resume_builder.service.FileUploadService;
import com.jatin.resume_builder.service.ResumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServlet;
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
	
	@PostMapping
	@Operation(summary = "Create Resume")
	public ResponseEntity<?> createResume(@Valid @RequestBody CreateResumeRequest request, Authentication authentication){
		
		Resume resume = resumeService.createResume(request, authentication.getPrincipal());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(resume);
	}
	
	@GetMapping
	@Operation(summary = "Get All Resumes")
	public ResponseEntity<?> getUserResumes(Authentication authentication){
		List<Resume> resumes = resumeService.getUserResumes(authentication.getPrincipal());
		
		return ResponseEntity.ok(resumes);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get Resume by UserId")
	public ResponseEntity<?> getResumeById(@PathVariable String id, Authentication authentication){
		Resume resume = resumeService.getResumeById(id, authentication.getPrincipal());
		
		return ResponseEntity.ok(resume);
	} 
	
	@PutMapping("/{id}")
	@Operation(summary = "Update Resume")
	public ResponseEntity<?> updateResume(@PathVariable String id, @RequestBody Resume updatedResume, Authentication authentication){
		Resume resume = resumeService.updateResume(id, updatedResume, authentication.getPrincipal());
		
		return ResponseEntity.ok(resume);
	}

	@PutMapping("/{id}/upload-images")
	@Operation(summary = "Upload images on Cloudinary")
	public ResponseEntity<?> uploadResumeImages(@PathVariable String id, 
												@RequestPart(value="thumbnail", required=true) MultipartFile thumbnail,
												@RequestPart(value="profileImage", required=false) MultipartFile profileImage,
												Authentication authentication) throws IOException{
	
		// call file service method
		Map<String, String> response = fileUploadService.uploadResumeImages(id, authentication.getPrincipal(), thumbnail, profileImage);
		
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete Resume")
	public ResponseEntity<?> deleteResume(@PathVariable String id, Authentication authentication){
		resumeService.deleteResume(id, authentication.getPrincipal());
		
		return ResponseEntity.ok(Map.of("message", "Resume deleted Successfully"));
	}
}
