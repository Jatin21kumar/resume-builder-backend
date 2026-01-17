package com.jatin.resume_builder.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jatin.resume_builder.document.Payment;

public interface PaymentRepository extends MongoRepository<Payment, String>{

	Optional<Payment> findByRazorpayOrderId(String orderId);
	
	Optional<Payment> findByRazorpayPaymentId(String paymentId);
	
	List<Payment> findByUserIdOrderByCreatedAtDesc(String userId);
	
	List<Payment> findByStatus(String status);
}
