package com.demo.welcomeSpring1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.demo.welcomeSpring1.entities.User;
import com.demo.welcomeSpring1.repositories.UserRepository;


@Service
public class UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;


	@Autowired
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
		passwordEncoder = new BCryptPasswordEncoder();
	}

	public User userRegister(User user) {
		if(userRepository.findByUsername(user.getUsername()).isPresent()) {

		//throw exception....
			throw new RuntimeException("username is already taken");
		}
		if(userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new RuntimeException("Email is already registred");
		}

		//user.setPassword(passwordEncoder.encode(user.getPassword()));
		String ppwd = user.getPassword();
		String epwd = passwordEncoder.encode(ppwd);
		user.setPassword(epwd);
		//write code to encode password
		return userRepository.save(user);


	}

}
