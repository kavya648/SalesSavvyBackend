package com.demo.welcomeSpring1.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.welcomeSpring1.entities.User;
import com.demo.welcomeSpring1.repositories.UserRepository;
import com.demo.welcomeSpring1.services.CartItemService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://loccalhost:5173", allowCredentials = "true")
public class CartController {

	@Autowired
     UserRepository userRepository;

	@Autowired
     CartItemService cartItemService;

    public CartController(UserRepository userRepository, CartItemService cartItemService) {
        this.userRepository = userRepository;
        this.cartItemService = cartItemService;
    }

    @GetMapping("/items/count")
    public ResponseEntity<Integer> getCartCount(@RequestParam String username) {
        int count = 0;
        Optional<User> opuser = userRepository.findByUsername(username);

        if (opuser != null) {  // Corrected from '!= null' to 'isPresent()'
            User user = opuser.get();
            count = cartItemService.getCartItemCount(user.getUserId());
        }

        return ResponseEntity.ok(count);
    }
    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request) {

    	User user = (User) request.getAttribute("authenticatedUser");
    	Map <String, Object> response = cartItemService.getCartItems(user.getUserId());
    	return ResponseEntity.ok(response);


    }

    @PostMapping("/add")
    @CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
    public ResponseEntity<Void> addToCart(@RequestBody Map<String,Object> request) {
    	String username = (String) request.get("username");
    	int productId = (int) request.get("productId");

    	int quantity = request.containsKey("quantity") ? (int) request.get("quantity") : 1;
    	User user = userRepository.findByUsername(username)
    			.orElseThrow(() -> new IllegalArgumentException("User not found with username:" + username));
    	cartItemService.addToCart(user.getUserId(), productId,quantity);
    	 return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update")
    public ResponseEntity<Void>updateCartItemQuantity(@RequestBody Map<String, Object> request) {
    	String username = (String) request.get("username");
    	int productId = (int) request.get("productId");
    	int quantity = (int) request.get("quantity");

    	User user = userRepository.findByUsername(username)
    			.orElseThrow(() -> new IllegalArgumentException("User not found with username:" + username));
    	cartItemService.updateCartItemQuantity(user.getUserId(), productId, quantity);
    	return ResponseEntity.status(HttpStatus.OK).build();


    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCartItem(@RequestBody Map<String, Object> request) {
    	String username = (String) request.get("username");
    	int productId = (int) request.get("productId");

    	User user = userRepository.findByUsername(username)
    			.orElseThrow(() -> new IllegalArgumentException("User not found with username:" + username));
    	cartItemService.deleteCartItem(user.getUserId(), productId);
    	return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
}
