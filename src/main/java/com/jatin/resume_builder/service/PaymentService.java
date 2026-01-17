package com.jatin.resume_builder.service;

import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jatin.resume_builder.document.Payment;
import com.jatin.resume_builder.document.User;
import com.jatin.resume_builder.dto.AuthResponse;
import com.jatin.resume_builder.repository.PaymentRepository;
import com.jatin.resume_builder.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

	private final PaymentRepository paymentRepository;
	
	private final UserService userService;
	
	private final UserRepository userRepository;
	
	@Value("${razorpay.key.id}")
	private String razorpayKeyId;
	@Value("${razorpay.key.secret}")
	private String razorpayKeySecret;
	
	public Payment createOrder(Object principal, String planType) throws RazorpayException {
		AuthResponse response = userService.getProfile(principal);
		
		// create razorpay client
		RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
		
		// prepare jsn object to pass razorpay
		int amount = 999; // in rupees
		String currency = "INR";
		String receipt = "Premium Plan" + "_" + UUID.randomUUID().toString().substring(0, 8);
		
		JSONObject orderRequest = new JSONObject();
		orderRequest.put("amount", amount);
		orderRequest.put("currency", currency);
		orderRequest.put("receipt", receipt);
		
		// call razorpay api to create order
		Order razorpayOrder = client.orders.create(orderRequest);
		
		// save order details to db
		Payment newPayment = Payment.builder()
				.userId(response.getId())
				.razorpayOrderId(razorpayOrder.get("id"))
				.amount(amount)
				.currency(currency)
				.planType(planType)
				.status("created")
				.receipt(receipt)
				.build();
		
		// return result
		return paymentRepository.save(newPayment);
	}

	public boolean verifyPayment(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) throws RazorpayException {
		try {
			JSONObject attributes = new JSONObject();
			attributes.put("razorpay_order_id", razorpayOrderId);
			attributes.put("razorpay_payment_id", razorpayPaymentId);
			attributes.put("razorpay_signature", razorpaySignature);
			
			boolean isValidSignature = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
			
			if(isValidSignature) {
				Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
													.orElseThrow(() -> new RuntimeException("Payment not found")); 
				
				payment.setRazorpayPaymentId(razorpayPaymentId);
				payment.setRazorpaySignature(razorpaySignature);
				payment.setStatus("paid");
				
				paymentRepository.save(payment);
				
				// update user subscription
				upgradeUserSubscription(payment.getUserId(), payment.getPlanType());
				return true;
			}
			return false;
		}
		catch(Exception e) {
			log.error("Error verifying the paymeny : ", e);
			return false;
		}
	}

	private void upgradeUserSubscription(String userId, String planType) {
		User existingUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		existingUser.setSubscritptionPlan(planType);
		userRepository.save(existingUser);
		log.info("User {} upgraded to {} plan ", userId, planType);
	}

	public List<Payment> getUserPayments(Object principal) {
		AuthResponse response = userService.getProfile(principal);
		
		return paymentRepository.findByUserIdOrderByCreatedAtDesc(response.getId());
	}

	public Payment getOrderDetails(String orderId) {
		return paymentRepository.findByRazorpayOrderId(orderId).orElseThrow(() -> new RuntimeException("Payment no found"));
	}
}
