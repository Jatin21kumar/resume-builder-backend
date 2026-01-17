package com.jatin.resume_builder.document;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

	private String id;
	
	private String userId;
	
	private String title;
	
	private String thumbnailLink;
	
	private Template template;
	
	private ProfileInfo profileInfo;
	
	private ContactInfo contactInfo;
	
	private List<WorkExperience> workExperience;
	
	private List<Education> educations;
	
	private List<Skill> skills;
	
	private List<Project>  projects;
	
	private List<Certification> certifications;

	private List<Language> languages;
	
	private List<String> hobbies;
	
	@CreatedDate
	private Instant createdAt;
	
	@LastModifiedDate
	private Instant updatedAt;
 	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Template{
		private String theme;
		private List<String> colorPalette;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ProfileInfo{
		private String profilePreviewUrl;
		private String fullName;
		private String designation;
		private String summary;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ContactInfo{
		private String email;
		private String phone;
		private String location;
		private String linkdIn;
		private String github;
		private String website;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class WorkExperience{
		private String company;
		private String role;
		private String startDate;
		private String endDate;
		private String description;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Education{
		private String degree;
		private String instituion;
		private String startDate;
		private String endDate;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Skill{
		private String name;
		private Integer progress;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Project{
		private String title;
		private String description;
		private String github;
		private String liveDemo;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Certification{
		private String title;
		private String issuer;
		private String year;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class Language{
		private String name;
		private Integer progress;
	}
}
