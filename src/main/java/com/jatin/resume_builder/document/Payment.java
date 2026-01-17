package com.jatin.resume_builder.document;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection="payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

	private String id;
	
	private String userId;
	private String razorpayOrderId;
	private String razorpayPaymentId;
	private String razorpaySignature;
	
	private Integer amount;
	private String currency;
	private String planType;
	
	@Builder.Default
	private String status = "created"; // created, paid, failed
	
	private String receipt;
	
	@CreatedDate
	private Instant createdAt;
	@LastModifiedDate
	private Instant updatedAt;
	
}
