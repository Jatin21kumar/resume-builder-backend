package com.jatin.resume_builder.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.jatin.resume_builder.dto.AuthResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {
	
	private final UserService userService;

	public Map<String, Object> getTemplates(Object principal){
		
		// get current profile
		AuthResponse authResponse = userService.getProfile(principal);
		
		// get available templates based on subscriptions
		List<String> availableTemplates;
		
		Boolean isPremium = "premium".equalsIgnoreCase(authResponse.getSubscriptionPlan());
		
		if(isPremium) {
			availableTemplates = List.of("01", "02", "03");
		}
		else availableTemplates = List.of("01");
				
		// add data into map
		Map<String, Object> restrictions = new HashMap<>();
		restrictions.put("availableTemplates", availableTemplates);
		restrictions.put("allTemplates", List.of("01", "02", "03"));
		restrictions.put("subscriptionPlan", authResponse.getSubscriptionPlan());
		restrictions.put("isPremium", isPremium);
		
		// return result
		return restrictions;
	}
}
