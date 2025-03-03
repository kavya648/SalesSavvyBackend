package com.demo.welcomeSpring1.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.welcomeSpring1.entities.User;
import com.demo.welcomeSpring1.services.OrderService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // Allow cross-origin requests
@RequestMapping("/api/orders")
public class OrderController {
	OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}


	    @GetMapping
	    public ResponseEntity<Map<String, Object>> getOrdersForUser(HttpServletRequest request) {
	        try {
	            // Retrieve the authenticated user from the request
	            User user = (User) request.getAttribute("authenticatedUser");

	            // Handle unauthenticated requests
	            if (user == null) {
	                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
	            }

	            // Fetch orders for the user via the service layer
	            Map<String, Object> response = orderService.getOrdersForUser(user);

	            // Return the response with HTTP 200 OK
	            return ResponseEntity.ok(response);
	        } catch (IllegalArgumentException e) {
	            // Handle cases where user details are invalid or missing
	            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
	      /*  } catch (Exception e) {
	            // Handle unexpected exceptions
	            e.printStackTrace();
	            return ResponseEntity.status(500).body(Map.of("error", "An unexpected error occurred"));
	        }*/
	    }
	}
}

