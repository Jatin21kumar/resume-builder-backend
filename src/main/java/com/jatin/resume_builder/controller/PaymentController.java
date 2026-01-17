package com.jatin.resume_builder.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jatin.resume_builder.document.Payment;
import com.jatin.resume_builder.service.PaymentService;
import com.jatin.resume_builder.service.UserService;
import com.razorpay.RazorpayException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/payment")
@Tag(name = "Payment Controller", description = "Handling Payment feature")
public class PaymentController {

	private final PaymentService paymentService;
	private final UserService userService;
	
	@PostMapping("/create-order")
	@Operation(summary = "Create Order for request")
	public ResponseEntity<?> createOrder(@RequestBody Map<String, String> request, Authentication authentication) throws RazorpayException{
		
		// vALIDATe the Request
		String planType = request.get("planType");
		if(!"premium".equalsIgnoreCase(planType)) {
			return ResponseEntity.badRequest().body(Map.of("message", "Invalid Plan Type"));
		}
		
		// call service method
		Payment payment = paymentService.createOrder(authentication.getPrincipal(), planType);
		
		// prepare response object
		Map<String, Object> response = Map.of(
					"orderId", payment.getRazorpayOrderId(),
					"amount", payment.getAmount(),
					"currency", payment.getCurrency(),
					"reciept", payment.getReceipt()
				);
		
		// return response
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/verify")
	@Operation(summary = "Verify payment credentials")
	public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> request) throws RazorpayException{
		String razorpayOrderId = request.get("razorpay_order_id");
		String razorpayPaymentId = request.get("razorpay_payment_id");
		String razorpaySignature = request.get("razorpay_signature");
		
		if(Objects.isNull(razorpayOrderId) || Objects.isNull(razorpayPaymentId) || Objects.isNull(razorpaySignature)) {
			return ResponseEntity.badRequest().body(Map.of("message", "Missing required payment parameters"));
		}
		
		boolean isValid = paymentService.verifyPayment(razorpayOrderId, razorpayPaymentId, razorpaySignature);
		
		if(isValid) {
			return ResponseEntity.ok(Map.of(
					"message", "Payment verified Successfully",
					"status", "Success"
					));
		}
		else {
			return ResponseEntity.badRequest().body(Map.of("message", "Payment verification failed"));
		}
	}
	
	@GetMapping("/history")
	@Operation(summary = "Get Payment History")
	public ResponseEntity<?> getPaymentHistory(Authentication authentication){
		List<Payment> payments = paymentService.getUserPayments(authentication.getPrincipal());
		
		return ResponseEntity.ok(payments);
	}
	
	@GetMapping("order/{orderId}")
	@Operation(summary = "Get order details")
	public ResponseEntity<?> getOrderDetails(@PathVariable String orderId){
		// call service method
		Payment paymentDetails = paymentService.getOrderDetails(orderId);
		
		// return response
		return ResponseEntity.ok(paymentDetails);
	}
	
	@PostMapping("/mock-success")
	public ResponseEntity<?> mockPayment(Authentication authentication) {
	    userService.changePlan(authentication.getPrincipal());
	    
	    return ResponseEntity.ok(Map.of(
	        "message", "Mock payment successful",
	        "plan", "premium"
	    ));
	}
}
