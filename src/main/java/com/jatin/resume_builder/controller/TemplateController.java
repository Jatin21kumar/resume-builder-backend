package com.jatin.resume_builder.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jatin.resume_builder.service.TemplateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/template")
@Slf4j
@Tag(name = "Template Controller", description = "Provide template based in subscription plan")
public class TemplateController {
	
	private final TemplateService templateService;

	@GetMapping
	@Operation(summary = "Get templates based on Plan")
	public ResponseEntity<?> getTemplates(Authentication authentication){
		// call service
		Map<String, Object> response = templateService.getTemplates(authentication.getPrincipal());
		
		// return response
		return ResponseEntity.ok(response);
	}
}
