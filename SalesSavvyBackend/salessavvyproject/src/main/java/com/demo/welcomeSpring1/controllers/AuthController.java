package com.demo.welcomeSpring1.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.welcomeSpring1.dtos.LoginDTO;
import com.demo.welcomeSpring1.entities.User;
import com.demo.welcomeSpring1.services.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	AuthService authService;
	public AuthController(AuthService authService) {
		this.authService = authService;
	}


	@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
	@PostMapping("/login")
	public ResponseEntity<?> loginUSer(@RequestBody LoginDTO usercred,HttpServletResponse response)   {

			try {
				User user = authService.authenticate(usercred.getUsername(), usercred.getPassword());
				String token = authService.generateToken(user);
				Cookie cookie = new Cookie("authToken", token);
				cookie.setHttpOnly(true);
				cookie.setSecure(false); // Set to true if using HTTPS
				cookie.setPath("/");
				cookie.setMaxAge(3600); // 1 hour
				cookie.setDomain("localhost");
				response.addCookie(cookie);
				// Optional but useful
				response.addHeader("Set-Cookie",
						String.format("authToken=%s; HttpOnly; Path=/; Max-Age=3600; SameSite=None", token));
				Map<String, Object> responseBody = new HashMap<>();
				responseBody.put("message", "Login successful");
				responseBody.put("role", user.getRole().name());
				responseBody.put("username", user.getUsername());
				return ResponseEntity.ok(responseBody);



		} catch (RuntimeException e)
		{
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error",e.getMessage()));
		}
	}
	@PostMapping("/logout")
	public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request,HttpServletResponse response) {

		try {
		User user = (User) request.getAttribute("authenticatedAuser");
		authService.logout(user);
		Cookie cookie = new Cookie("autgToken", null);
		cookie.setHttpOnly(true);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		response.addCookie(cookie);

		Map<String, Object> responseBody  = new HashMap<>();
		responseBody.put("mesaage", "Logout Successful");

		return ResponseEntity.ok(responseBody);
	}
		catch(Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("MESSAGE", "LOGOUT FAILED");
			return ResponseEntity.status(500).body(error);
		}
	}

}
